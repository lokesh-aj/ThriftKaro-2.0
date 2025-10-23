package com.orderservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "couponcodes")
@Data
@AllArgsConstructor
@Builder
public class CouponCode {
    
    @Id
    private String id;
    
    private String name;
    private Double value;
    private Double minAmount;
    private Double maxAmount;
    private String shopId;
    private String selectedProduct;
    private LocalDateTime createdAt;
    
    public CouponCode() {
        this.createdAt = LocalDateTime.now();
    }
}

