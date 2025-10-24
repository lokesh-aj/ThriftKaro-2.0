package com.cart.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "cart_items")
public class CartItem {
    
    @Id
    private String cartItemId;
    
    private Cart cart;
    
    private Long productId;
    
    private Integer quantity;
}









