package com.cart.repository;

import com.cart.entities.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends MongoRepository<Cart, String> {
    
    // Find all carts for a user (handles duplicate carts)
    List<Cart> findAllByUserId(String userId);
    
    // Find the most recent cart for a user
    Optional<Cart> findFirstByUserIdOrderByCreatedAtDesc(String userId);
    
    boolean existsByUserId(String userId);
}



