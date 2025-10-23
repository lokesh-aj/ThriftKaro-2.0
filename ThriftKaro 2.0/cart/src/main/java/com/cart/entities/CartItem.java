package com.cart.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    
    private String productId;
    
    private Integer quantity;
    
    private String productName;
    
    private Double price;
    
    private String image;
}









