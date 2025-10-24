package com.shopservice.service;

import com.shopservice.client.ProductClient;
import com.shopservice.entities.Shop;
import com.shopservice.repository.ShopRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShopService {

    private final ShopRepository shopRepository;
    private final ProductClient productClient;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ShopService(ShopRepository shopRepository, ProductClient productClient) {
        this.shopRepository = shopRepository;
        this.productClient = productClient;
    }

    public Shop register(Shop shop) {
        shop.setPassword(passwordEncoder.encode(shop.getPassword()));
        return shopRepository.save(shop);
    }

    public Map<String, Object> login(String email, String password) {
        Shop shop = shopRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(password, shop.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        Map<String, Object> res = new HashMap<>();
        res.put("shopId", shop.getId());
        res.put("email", shop.getEmail());
        return res;
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
}




