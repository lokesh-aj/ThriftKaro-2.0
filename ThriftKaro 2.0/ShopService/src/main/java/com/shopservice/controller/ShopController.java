package com.shopservice.controller;

import com.shopservice.entities.Shop;
import com.shopservice.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/shop")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ShopService UP");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Shop shop) {
        try {
            Map<String, Object> response = shopService.register(shop);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            Map<String, Object> response = shopService.login(body.get("email"), body.get("password"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(401).body(errorResponse);
        }
    }

    @GetMapping("/details/{shopId}")
    public ResponseEntity<Shop> details(@PathVariable String shopId) {
        return ResponseEntity.ok(shopService.details(shopId));
    }

    @PutMapping("/update/{shopId}")
    public ResponseEntity<Shop> update(@PathVariable String shopId, @RequestBody Shop updates) {
        return ResponseEntity.ok(shopService.update(shopId, updates));
    }

    @GetMapping("/products/{shopId}")
    public ResponseEntity<List<Map<String, Object>>> products(@PathVariable String shopId) {
        return ResponseEntity.ok(shopService.listSellerProducts(shopId));
    }

    @GetMapping("/admin-all-sellers")
    public ResponseEntity<?> getAllSellersForAdmin(@RequestHeader("Authorization") String token) {
        try {
            List<Shop> sellers = shopService.getAllSellers();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sellers", sellers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to fetch sellers: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}




