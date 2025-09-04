package com.userservice.dtos;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String shippingAddress;
    private String billingAddress;
}
