package com.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    private String cartItemId;
    private String productId;
    private String productName;
    private Double productPrice;
    private Integer quantity;
    private Double subtotal;
    private ProductResponse product;  // Full product object for frontend
}









