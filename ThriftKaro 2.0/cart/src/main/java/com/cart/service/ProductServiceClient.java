package com.cart.service;

import com.cart.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProductServiceClient {
    
    @Value("${product.service.url:http://localhost:8081}")
    private String productServiceUrl;
    
    private final RestTemplate restTemplate;
    
    public ProductServiceClient() {
        this.restTemplate = new RestTemplate();
    }
    
    public ProductResponse getProductById(Long productId, String token) {
        try {
            // Product Service /products/{id} endpoint is public, no authentication needed
            String url = productServiceUrl + "/products/" + productId;
            ResponseEntity<ProductResponse> response = restTemplate.getForEntity(url, ProductResponse.class);
            
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch product: " + e.getMessage());
        }
    }
    
    public boolean isProductAvailable(Long productId, Integer quantity, String token) {
        try {
            ProductResponse product = getProductById(productId, token);
            return product != null && product.getStockQuantity() >= quantity;
        } catch (Exception e) {
            return false;
        }
    }
}


