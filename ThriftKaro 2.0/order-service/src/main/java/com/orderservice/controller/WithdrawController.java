package com.orderservice.controller;

import com.orderservice.dto.CreateWithdrawRequest;
import com.orderservice.entities.Withdraw;
import com.orderservice.service.WithdrawService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/withdraw")
@RequiredArgsConstructor
@Slf4j
public class WithdrawController {

    private final WithdrawService withdrawService;

    @PostMapping("/create-withdraw-request")
    public ResponseEntity<Map<String, Object>> createWithdrawRequest(
            @RequestBody CreateWithdrawRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            // Extract seller info from token (this would be done by JWT filter in real implementation)
            Map<String, Object> seller = new HashMap<>();
            seller.put("id", "seller123");
            seller.put("name", "Test Seller");
            seller.put("email", "seller@test.com");
            
            Withdraw withdraw = withdrawService.createWithdrawRequest(request, seller);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("withdraw", withdraw);
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            log.error("Error creating withdraw request: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/get-all-withdraw-request")
    public ResponseEntity<Map<String, Object>> getAllWithdrawRequests(@RequestHeader("Authorization") String token) {
        try {
            List<Withdraw> withdraws = withdrawService.getAllWithdraws();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("withdraws", withdraws);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting withdraw requests: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/update-withdraw-request/{id}")
    public ResponseEntity<Map<String, Object>> updateWithdrawRequest(
            @PathVariable String id,
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String token) {
        try {
            String status = (String) request.get("status");
            Withdraw withdraw = withdrawService.updateWithdrawStatus(id, status);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("withdraw", withdraw);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating withdraw request: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}

