package com.orderservice.controller;

import com.orderservice.dto.CreateCouponRequest;
import com.orderservice.entities.CouponCode;
import com.orderservice.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/coupon")
@RequiredArgsConstructor
@Slf4j
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/create-coupon-code")
    public ResponseEntity<Map<String, Object>> createCouponCode(
            @RequestBody CreateCouponRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            CouponCode coupon = couponService.createCoupon(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("couponCode", coupon);
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
//            log.error("Error creating coupon code: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    @GetMapping("/get-coupon/{id}")
    public ResponseEntity<Map<String, Object>> getCouponsByShopId(
            @PathVariable String id,
            @RequestHeader("Authorization") String token) {
        try {
            List<CouponCode> couponCodes = couponService.getCouponsByShopId(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("couponCodes", couponCodes);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
//            log.error("Error getting coupons for shop {}: {}", id, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    @DeleteMapping("/delete-coupon/{id}")
    public ResponseEntity<Map<String, Object>> deleteCoupon(
            @PathVariable String id,
            @RequestHeader("Authorization") String token) {
        try {
            couponService.deleteCoupon(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Coupon code deleted successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
//            log.error("Error deleting coupon: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    @GetMapping("/get-coupon-value/{name}")
    public ResponseEntity<Map<String, Object>> getCouponValue(@PathVariable String name) {
        try {
            Optional<CouponCode> couponCode = couponService.getCouponByName(name);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("couponCode", couponCode.orElse(null));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
//            log.error("Error getting coupon value: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }
}

