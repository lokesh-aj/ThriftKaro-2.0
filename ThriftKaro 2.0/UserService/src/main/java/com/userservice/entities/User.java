package com.userservice.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String passwordHash;

    private String phoneNumber;

    private List<Map<String, Object>> addresses;

    private String role = "USER";

    private Map<String, Object> avatar;

    private LocalDateTime createdAt;

    private String resetPasswordToken;

    private LocalDateTime resetPasswordTime;

    public User() {
        this.role = "USER";
        this.createdAt = LocalDateTime.now();
    }
}
