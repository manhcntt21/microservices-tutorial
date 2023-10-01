package com.example.orderservice.repository;

import com.example.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author manhdt14
 * created in 10/1/2023 9:18 PM
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
