package com.example.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author manhdt14
 * created in 10/2/2023 12:50 AM
 */
@Entity
@Table(name = "t_inventory")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Inventory {
    @Id
    @SequenceGenerator(name = "inventory_id_sequence", sequenceName = "inventory_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inventory_id_sequence")
    private Long id;
    private String skuCode;
    private Integer quantity;
}
