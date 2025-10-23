package com.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {
    private List<Map<String, Object>> cart;
    private Map<String, Object> shippingAddress;
    private Map<String, Object> user;
    private Double totalPrice;
    private Map<String, Object> paymentInfo;
}

