package com.example.inventoryservice;

import com.example.inventoryservice.entity.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    /**
     * khi khoi dong luon luon load du lieu
     * @param inventoryRepository
     * @return
     */
    @Bean
    public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
        return args -> {
            Inventory inventory = new Inventory();
            inventory.setSkuCode("iphone_13");
            inventory.setQuantity(100);

            Inventory inventory1 = new Inventory();
            inventory1.setSkuCode("iphone_13_red");
            inventory1.setQuantity(0);

            Inventory inventory2 = new Inventory();
            inventory1.setSkuCode("iphone_13_blue");
            inventory1.setQuantity(50);

            inventoryRepository.save(inventory);
            inventoryRepository.save(inventory1);
            inventoryRepository.save(inventory2);
        };
    }
}
