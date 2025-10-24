package com.shopservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "ProductService")
public interface ProductClient {
    @GetMapping("/api/v2/product/seller/{shopId}")
    List<Map<String, Object>> getProductsBySeller(@PathVariable("shopId") String shopId);
}




