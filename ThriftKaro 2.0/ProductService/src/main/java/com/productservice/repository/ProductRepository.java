package com.productservice.repository;

import com.productservice.entities.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    
    List<Product> findByCategory(String category);
    
    List<Product> findByShopId(String shopId);
    
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<Product> findByNameContainingIgnoreCase(String name);
    
    @Query("{'category': ?0, 'stock': {$gt: 0}}")
    List<Product> findByCategoryAndStockGreaterThan(String category, Integer stock);
    
    Optional<Product> findById(String id);
    
    boolean existsById(String id);
}
