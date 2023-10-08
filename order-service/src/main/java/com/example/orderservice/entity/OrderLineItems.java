package com.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "t_order_line_items")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineItems {
    @Id
    @SequenceGenerator(name = "order_item_sequence", sequenceName = "order_item_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_sequence")
    private Long id;
    private String skuCode;
    private BigDecimal price;
    private Integer quantity;
}