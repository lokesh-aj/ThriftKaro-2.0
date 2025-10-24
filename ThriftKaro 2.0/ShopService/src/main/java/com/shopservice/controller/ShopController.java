package com.shopservice.controller;

import com.shopservice.entities.Shop;
import com.shopservice.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Shop> register(@RequestBody Shop shop) {
        return ResponseEntity.ok(shopService.register(shop));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(shopService.login(body.get("email"), body.get("password")));
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
}




