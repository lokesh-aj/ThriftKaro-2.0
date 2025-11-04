package com.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private BigDecimal originalPrice;
    private BigDecimal discountPrice;
    private BigDecimal price;
    private Integer stock;
    private Integer stockQuantity;
    private List<Map<String, Object>> images;
    private String shopId;  // Added for coupon validation
}


