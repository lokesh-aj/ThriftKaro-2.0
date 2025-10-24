package com.userservice.controller;

import com.userservice.dtos.LoginRequest;
import com.userservice.dtos.RegisterRequest;
import com.userservice.dtos.AuthResponse;
import com.userservice.entities.User;
import com.userservice.security.JwtUtil;
import com.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // -------- REGISTER --------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userService.findByEmail(request.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPassword());
        
        // Create addresses list with shipping and billing addresses
        List<Map<String, Object>> addresses = new ArrayList<>();
        if (request.getShippingAddress() != null) {
            Map<String, Object> shippingAddress = new HashMap<>();
            shippingAddress.put("type", "shipping");
            shippingAddress.put("address", request.getShippingAddress());
            addresses.add(shippingAddress);
        }
        if (request.getBillingAddress() != null) {
            Map<String, Object> billingAddress = new HashMap<>();
            billingAddress.put("type", "billing");
            billingAddress.put("address", request.getBillingAddress());
            addresses.add(billingAddress);
        }
        user.setAddresses(addresses);

        userService.register(user);
        return ResponseEntity.ok("User registered successfully: " + user.getEmail());
    }

    // -------- LOGIN --------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userService.findByEmail(request.getEmail());
        if (user == null || !userService.checkPassword(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    // -------- PROTECTED ENDPOINT (ME) --------
    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(token);
            User user = userService.findByEmail(email);
            if (user == null) return ResponseEntity.status(404).body("User not found");
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }
    }
}
