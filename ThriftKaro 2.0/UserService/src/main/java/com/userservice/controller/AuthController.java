package com.userservice.controller;

import com.userservice.dtos.LoginRequest;
import com.userservice.dtos.RegisterRequest;
import com.userservice.dtos.AuthResponse;
import com.userservice.entities.User;
import com.userservice.security.JwtUtil;
import com.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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
            
            ResponseCookie cookie = ResponseCookie.from("tk_token", token)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .path("/")
                    .maxAge(86400)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response);
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

            // If the stored password was plaintext (legacy), upgrade to bcrypt transparently
            if (!userService.isEncoded(user.getPasswordHash())) {
                user.setPasswordHash(userService.encodePassword(request.getPassword()));
                userService.save(user);
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
            
            ResponseCookie cookie = ResponseCookie.from("tk_token", token)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .path("/")
                    .maxAge(86400)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // -------- LOGOUT (clear cookie) --------
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cleared = ResponseCookie.from("tk_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleared.toString())
                .body(Map.of("success", true));
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

    // -------- UPDATE PROFILE (name/email/phone) --------
    @PutMapping("/update-me")
    public ResponseEntity<?> updateMe(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> updates
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(token);
            User user = userService.findByEmail(email);
            if (user == null) return ResponseEntity.status(404).body("User not found");

            if (updates.containsKey("name")) user.setName((String) updates.get("name"));
            if (updates.containsKey("email")) user.setEmail((String) updates.get("email"));
            if (updates.containsKey("phoneNumber")) user.setPhoneNumber((String) updates.get("phoneNumber"));

            User saved = userService.save(user);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Update failed: " + e.getMessage());
        }
    }

    // -------- UPDATE AVATAR --------
    @PutMapping("/update-avatar")
    public ResponseEntity<?> updateAvatar(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> body
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(token);
            User user = userService.findByEmail(email);
            if (user == null) return ResponseEntity.status(404).body("User not found");

            Object avatar = body.get("avatar");
            if (avatar instanceof String && !((String) avatar).isEmpty()) {
                Map<String, Object> avatarMap = new HashMap<>();
                avatarMap.put("url", avatar);
                user.setAvatar(avatarMap);
            }
            User saved = userService.save(user);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Avatar update failed: " + e.getMessage());
        }
    }

    // -------- UPDATE PASSWORD --------
    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(token);
            User user = userService.findByEmail(email);
            if (user == null) return ResponseEntity.status(404).body("User not found");

            String oldPassword = body.get("oldPassword");
            String newPassword = body.get("newPassword");
            String confirmPassword = body.get("confirmPassword");

            if (oldPassword == null || newPassword == null || confirmPassword == null)
                return ResponseEntity.badRequest().body("Missing password fields");
            if (!userService.checkPassword(oldPassword, user.getPasswordHash()))
                return ResponseEntity.status(401).body(Map.of("message", "Old password incorrect"));
            if (!newPassword.equals(confirmPassword))
                return ResponseEntity.badRequest().body(Map.of("message", "Passwords do not match"));

            user.setPasswordHash(userService.encodePassword(newPassword));
            userService.save(user);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Password update failed: " + e.getMessage());
        }
    }
}
