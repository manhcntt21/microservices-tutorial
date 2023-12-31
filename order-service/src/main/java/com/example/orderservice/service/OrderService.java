package com.example.orderservice.service;

import com.example.orderservice.dto.InventoryResponse;
import com.example.orderservice.dto.OrderLineItemsDto;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderLineItems;
import com.example.orderservice.event.OrderPlacedEvent;
import com.example.orderservice.repository.OrderRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author manhdt14
 * created in 10/1/2023 9:09 PM
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    private final WebClient.Builder webclientBuilder;
    private final ObservationRegistry observationRegistry;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
//        orderRequest.getOrderLineItemsDtoList()
//                .stream()
//                .map(orderLineItemsDto -> mapToDto(orderLineItemsDto));
        order.setOrderLineItemsList(orderLineItems);
        // todo: get all skuCode
        List<String> skuCodes = order.getOrderLineItemsList()
                .stream()
                .map(orderLineItem -> orderLineItem.getSkuCode())
                .toList();
//        List<String> skuCodes = order.getOrderLineItemsList()
//                .stream()
//                .map(OrderLineItems::getSkuCode)
//                .toList();
        // call stock
        Observation inventoryServiceObservation = Observation.createNotStarted("inventory-service-lookup",
                this.observationRegistry);
        inventoryServiceObservation.lowCardinalityKeyValue("call", "inventory-service");
        return inventoryServiceObservation.observe(() -> {
            // todo: call inventory service and place order if product is in stock
            InventoryResponse[] inventoryResponseArray = webclientBuilder.build().get()
                    .uri("http://INVENTORY-SERVICE/api/v1/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();

//        Arrays.stream(inventoryResponseArray).allMatch(inventoryResponse -> inventoryResponse.isInSock());
            boolean allProductIsInStock = Arrays.stream(inventoryResponseArray)
                    .allMatch(InventoryResponse::isInSock);
            // todo: if all products is in stock, save order
            if(allProductIsInStock) {
                orderRepository.save(order);
                // todo: produce messgae
                kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
                return "order placed successfully";
            } else {
                throw new IllegalArgumentException("Product is not in stock, please try again");
            }
        });
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
