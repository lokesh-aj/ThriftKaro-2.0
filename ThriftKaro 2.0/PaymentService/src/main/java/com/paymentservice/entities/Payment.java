package com.paymentservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    private String id;

    private String orderId;
    private String userId;

    private BigDecimal amount;

    private String status; // e.g., INITIATED, SUCCESS, FAILED, REFUNDED

    private String paymentMethod;

    private String transactionId;

    private LocalDateTime timestamp;
}




