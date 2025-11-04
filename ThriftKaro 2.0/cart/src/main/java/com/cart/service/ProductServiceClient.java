package com.cart.service;

import com.cart.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceClient {
    
    @Value("${product.service.url:http://localhost:8089}")
    private String productServiceUrl;
    
    private final RestTemplate restTemplate;
    
    public ProductServiceClient() {
        this.restTemplate = new RestTemplate();
    }
    
    public ProductResponse getProductById(String productId, String token) {
        try {
            // Product Service endpoint: /api/v2/product/{id}
            String url = productServiceUrl + "/api/v2/product/" + productId;
            
            HttpHeaders headers = new HttpHeaders();
            if (token != null && !token.isBlank()) {
                headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            }
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            // Use Map to handle the response, then convert to ProductResponse
            ResponseEntity<java.util.Map<String, Object>> response = restTemplate.exchange(
                    url, HttpMethod.GET, requestEntity,
                    (Class<java.util.Map<String, Object>>)(Class<?>)java.util.Map.class);
            java.util.Map<String, Object> productMap = response.getBody();
            
            if (productMap == null) {
                System.err.println("ProductServiceClient - Product not found for ID: " + productId);
                return null;
            }
            
            // Convert Map to ProductResponse
            String productIdStr = (String) productMap.get("id");
            
            // Extract images
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> images = (List<Map<String, Object>>) productMap.get("images");
            
            // Extract prices
            BigDecimal originalPrice = productMap.get("originalPrice") != null ? 
                    new java.math.BigDecimal(productMap.get("originalPrice").toString()) : null;
            BigDecimal discountPrice = productMap.get("discountPrice") != null ? 
                    new java.math.BigDecimal(productMap.get("discountPrice").toString()) : null;
            
            // Use discountPrice if available, otherwise originalPrice, otherwise zero
            BigDecimal price = discountPrice != null ? discountPrice : 
                             (originalPrice != null ? originalPrice : java.math.BigDecimal.ZERO);
            
            // Extract stock
            Integer stock = productMap.get("stock") != null ? 
                          ((Number) productMap.get("stock")).intValue() : 0;
            
            // Extract shopId
            String shopId = null;
            if (productMap.get("shopId") != null) {
                shopId = productMap.get("shopId").toString();
            } else if (productMap.get("shop") != null) {
                // Handle nested shop object
                @SuppressWarnings("unchecked")
                Map<String, Object> shop = (Map<String, Object>) productMap.get("shop");
                if (shop != null && shop.get("_id") != null) {
                    shopId = shop.get("_id").toString();
                } else if (shop != null && shop.get("id") != null) {
                    shopId = shop.get("id").toString();
                }
            }
            
            ProductResponse productResponse = ProductResponse.builder()
                    .id(productIdStr)
                    .name((String) productMap.get("name"))
                    .description((String) productMap.get("description"))
                    .originalPrice(originalPrice)
                    .discountPrice(discountPrice)
                    .price(price)
                    .stock(stock)
                    .stockQuantity(stock)
                    .images(images)
                    .shopId(shopId)
                    .build();
            
            return productResponse;
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            System.err.println("ProductServiceClient - Product not found (404): " + productId);
            return null;
        } catch (Exception e) {
            // Do NOT propagate as RuntimeException; returning null lets callers continue gracefully
            System.err.println("ProductServiceClient - Error fetching product (non-fatal): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean isProductAvailable(String productId, Integer quantity, String token) {
        try {
            ProductResponse product = getProductById(productId, token);
            return product != null && product.getStockQuantity() >= quantity;
        } catch (Exception e) {
            return false;
        }
    }
}


