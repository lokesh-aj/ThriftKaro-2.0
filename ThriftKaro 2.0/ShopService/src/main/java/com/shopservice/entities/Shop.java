package com.shopservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "shops")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shop {
    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String password;

    private String description;

    private String address;

    private String phoneNumber;

    private String role = "Seller";

    private Map<String, Object> avatar;

    private String zipCode;

    private Map<String, Object> withdrawMethod;

    private Double availableBalance;

    private List<Map<String, Object>> transactions;

    private LocalDateTime createdAt;

    private String resetPasswordToken;

    private LocalDateTime resetPasswordTime;

    public Shop() {
        this.role = "Seller";
        this.availableBalance = 0.0;
        this.createdAt = LocalDateTime.now();
    }
}




