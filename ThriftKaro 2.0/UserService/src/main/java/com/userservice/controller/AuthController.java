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
        try {
            if (userService.findByEmail(request.getEmail()) != null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "User already exists with this email!");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPasswordHash(request.getPassword());
            
            // Handle avatar if provided
            if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
                Map<String, Object> avatarMap = new HashMap<>();
                avatarMap.put("url", request.getAvatar());
                user.setAvatar(avatarMap);
            }
            
            // Create addresses list with shipping and billing addresses
            List<Map<String, Object>> addresses = new ArrayList<>();
            if (request.getShippingAddress() != null && !request.getShippingAddress().isEmpty()) {
                Map<String, Object> shippingAddress = new HashMap<>();
                shippingAddress.put("type", "shipping");
                shippingAddress.put("address", request.getShippingAddress());
                addresses.add(shippingAddress);
            }
            if (request.getBillingAddress() != null && !request.getBillingAddress().isEmpty()) {
                Map<String, Object> billingAddress = new HashMap<>();
                billingAddress.put("type", "billing");
                billingAddress.put("address", request.getBillingAddress());
                addresses.add(billingAddress);
            }
            user.setAddresses(addresses);

            User savedUser = userService.register(user);
            
            // Generate JWT token with userId included (same as login)
            String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getId());
            
            // Prepare user object for response (without password)
            Map<String, Object> userResponse = new HashMap<>();
            userResponse.put("_id", savedUser.getId());
            userResponse.put("name", savedUser.getName());
            userResponse.put("email", savedUser.getEmail());
            userResponse.put("role", savedUser.getRole());
            userResponse.put("avatar", savedUser.getAvatar());
            userResponse.put("addresses", savedUser.getAddresses());
            userResponse.put("createdAt", savedUser.getCreatedAt());
            
            // Prepare complete response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User registered successfully!");
            response.put("token", token);
            response.put("user", userResponse);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // -------- LOGIN --------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.findByEmail(request.getEmail());
            if (user == null || !userService.checkPassword(request.getPassword(), user.getPasswordHash())) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Invalid email or password");
                return ResponseEntity.status(401).body(errorResponse);
            }

            // Generate token with userId included (as String to match MongoDB ObjectId format)
            String token = jwtUtil.generateToken(user.getEmail(), user.getId());
            
            // Prepare user object for response (without password)
            Map<String, Object> userResponse = new HashMap<>();
            userResponse.put("_id", user.getId());
            userResponse.put("name", user.getName());
            userResponse.put("email", user.getEmail());
            userResponse.put("role", user.getRole());
            userResponse.put("avatar", user.getAvatar());
            userResponse.put("addresses", user.getAddresses());
            userResponse.put("createdAt", user.getCreatedAt());
            
            // Prepare complete response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful!");
            response.put("token", token);
            response.put("user", userResponse);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
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
