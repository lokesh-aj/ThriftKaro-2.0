package com.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCouponRequest {
    private String name;
    private Double value;
    private Double minAmount;
    private Double maxAmount;
    private String shopId;
    private String selectedProduct;
}

