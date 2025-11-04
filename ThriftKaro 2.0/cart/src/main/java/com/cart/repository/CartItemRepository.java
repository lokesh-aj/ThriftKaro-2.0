package com.cart.repository;

import com.cart.entities.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends MongoRepository<CartItem, String> {
    
    Optional<CartItem> findByCartIdAndProductId(String cartId, String productId);
    
    List<CartItem> findByCartId(String cartId);
    
    void deleteByCartIdAndProductId(String cartId, String productId);
    
    void deleteByCartId(String cartId);
}
