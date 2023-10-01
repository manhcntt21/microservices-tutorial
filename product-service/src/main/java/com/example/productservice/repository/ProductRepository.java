package com.example.productservice.repository;

import com.example.productservice.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author manhdt14
 * created in 10/1/2023 8:34 PM
 */
@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
}
