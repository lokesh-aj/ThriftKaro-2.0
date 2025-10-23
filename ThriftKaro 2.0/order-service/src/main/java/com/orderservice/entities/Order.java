package com.orderservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "orders")
@Data
@AllArgsConstructor
@Builder
public class Order {
    
    @Id
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
    
    public Order() {
        this.status = "Processing";
        this.paidAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }
}