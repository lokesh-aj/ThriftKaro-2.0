package com.cart.repository;

import com.cart.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    List<CartItem> findByCartCartId(Long cartId);
    
    Optional<CartItem> findByCartCartIdAndProductId(Long cartId, Long productId);
    
    void deleteByCartCartIdAndProductId(Long cartId, Long productId);
    
    void deleteByCartCartId(Long cartId);
}







