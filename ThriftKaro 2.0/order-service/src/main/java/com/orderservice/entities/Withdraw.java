package com.orderservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "withdraws")
@Data
@AllArgsConstructor
@Builder
public class Withdraw {
    
    @Id
    private String id;
    
    private Map<String, Object> seller;
    private Double amount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Withdraw() {
        this.status = "Processing";
        this.createdAt = LocalDateTime.now();
    }
}

