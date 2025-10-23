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
public class OrderConfirmationRequest {
    private String orderId;
    private String userEmail;
    private List<Map<String, Object>> orderDetails;
    private Double totalPrice;
    private Map<String, Object> shippingAddress;
    private String status;
}
