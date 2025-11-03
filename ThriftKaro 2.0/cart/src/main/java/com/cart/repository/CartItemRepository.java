package com.cart.repository;

import com.cart.entities.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends MongoRepository<CartItem, String> {
    
    Optional<CartItem> findByCartCartIdAndProductId(String cartId, String productId);
    
    List<CartItem> findByCartCartId(String cartId);
    
    void deleteByCartCartIdAndProductId(String cartId, String productId);
    
    void deleteByCartCartId(String cartId);
}
