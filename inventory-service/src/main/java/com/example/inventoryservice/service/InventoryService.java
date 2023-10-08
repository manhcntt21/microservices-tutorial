package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author manhdt14
 * created in 10/2/2023 12:56 AM
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    @SneakyThrows
    public List<InventoryResponse> isInStock(List<String> skuCode) {
        /**
         * mô tả tình huống hiệu năng database kém, vượt quá timout của circuit breaker là 3s
         * trong application.properties
         * khi thay thời gian sleep nhỏ hơn timeout-duration trong application.properties thì không xảy ra timeout nữa
         * ví dụ 2s
         */
        log.info("checking in stock");
        log.info("wait started");
//        Thread.sleep(10000);
        Thread.sleep(2000);
        log.info("wait ended");
        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                .map(inventory ->
                    InventoryResponse.builder()
                            .skuCode(inventory.getSkuCode())
                            .isInSock(inventory.getQuantity() > 0)
                            .build()
                )
                .toList();
    }


}
