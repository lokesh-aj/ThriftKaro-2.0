package com.orderservice.dto;

import com.orderservice.entities.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private String id;
    private List<Map<String, Object>> cart;
    private Map<String, Object> shippingAddress;
    private Map<String, Object> user;
    private Double totalPrice;
    private String status;
    private Map<String, Object> paymentInfo;
    private LocalDateTime paidAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;

    public static OrderResponse fromOrder(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .cart(order.getCart())
                .shippingAddress(order.getShippingAddress())
                .user(order.getUser())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .paymentInfo(order.getPaymentInfo())
                .paidAt(order.getPaidAt())
                .deliveredAt(order.getDeliveredAt())
                .createdAt(order.getCreatedAt())
                .build();
    }
}

