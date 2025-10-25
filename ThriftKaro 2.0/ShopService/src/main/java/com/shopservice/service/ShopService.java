package com.shopservice.service;

import com.shopservice.client.ProductClient;
import com.shopservice.entities.Shop;
import com.shopservice.repository.ShopRepository;
import com.shopservice.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShopService {

    private final ShopRepository shopRepository;
    private final ProductClient productClient;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ShopService(ShopRepository shopRepository, ProductClient productClient, JwtUtil jwtUtil) {
        this.shopRepository = shopRepository;
        this.productClient = productClient;
        this.jwtUtil = jwtUtil;
    }

    public Map<String, Object> register(Shop shop) {
        // Check if shop already exists
        if (shopRepository.findByEmail(shop.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Shop already exists with this email!");
        }
        
        // Handle avatar if provided
        if (shop.getAvatar() == null && shop.getPassword() != null) {
            // If avatar is sent as string, convert to Map
            Map<String, Object> avatarMap = new HashMap<>();
            avatarMap.put("url", "");
            shop.setAvatar(avatarMap);
        }
        
        shop.setPassword(passwordEncoder.encode(shop.getPassword()));
        Shop savedShop = shopRepository.save(shop);
        
        // Generate JWT token
        String token = jwtUtil.generateToken(savedShop.getEmail());
        
        // Prepare seller object for response (without password)
        Map<String, Object> sellerResponse = new HashMap<>();
        sellerResponse.put("_id", savedShop.getId());
        sellerResponse.put("name", savedShop.getName());
        sellerResponse.put("email", savedShop.getEmail());
        sellerResponse.put("role", savedShop.getRole());
        sellerResponse.put("avatar", savedShop.getAvatar());
        sellerResponse.put("address", savedShop.getAddress());
        sellerResponse.put("phoneNumber", savedShop.getPhoneNumber());
        sellerResponse.put("zipCode", savedShop.getZipCode());
        sellerResponse.put("availableBalance", savedShop.getAvailableBalance());
        sellerResponse.put("createdAt", savedShop.getCreatedAt());
        
        // Prepare complete response
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Shop registered successfully!");
        response.put("token", token);
        response.put("seller", sellerResponse);
        
        return response;
    }

    public Map<String, Object> login(String email, String password) {
        Shop shop = shopRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!passwordEncoder.matches(password, shop.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        // Generate JWT token
        String token = jwtUtil.generateToken(shop.getEmail());
        
        // Prepare seller object for response (without password)
        Map<String, Object> sellerResponse = new HashMap<>();
        sellerResponse.put("_id", shop.getId());
        sellerResponse.put("name", shop.getName());
        sellerResponse.put("email", shop.getEmail());
        sellerResponse.put("role", shop.getRole());
        sellerResponse.put("avatar", shop.getAvatar());
        sellerResponse.put("address", shop.getAddress());
        sellerResponse.put("phoneNumber", shop.getPhoneNumber());
        sellerResponse.put("zipCode", shop.getZipCode());
        sellerResponse.put("availableBalance", shop.getAvailableBalance());
        sellerResponse.put("createdAt", shop.getCreatedAt());
        
        // Prepare complete response
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login successful!");
        response.put("token", token);
        response.put("seller", sellerResponse);
        
        return response;
    }

    public Shop details(String shopId) {
        return shopRepository.findById(shopId).orElseThrow(() -> new IllegalArgumentException("Shop not found"));
    }

    public Shop update(String shopId, Shop updates) {
        Shop shop = details(shopId);
        if (updates.getName() != null) shop.setName(updates.getName());
        if (updates.getAddress() != null) shop.setAddress(updates.getAddress());
        return shopRepository.save(shop);
    }

    public List<Map<String, Object>> listSellerProducts(String shopId) {
        return productClient.getProductsBySeller(shopId);
    }

    public List<Shop> getAllSellers() {
        return shopRepository.findAll();
    }
}




