package com.orderservice.controller;

import com.orderservice.dto.CreateOrderRequest;
import com.orderservice.dto.UpdateOrderStatusRequest;
import com.orderservice.entities.Order;
import com.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            List<Order> orders = orderService.createOrder(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orders", orders);
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/get-all-orders/{userId}")
    public ResponseEntity<Map<String, Object>> getAllOrdersByUserId(@PathVariable String userId) {
        try {
            List<Order> orders = orderService.getAllOrdersByUserId(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orders", orders);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting orders for user {}: {}", userId, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/get-seller-all-orders/{shopId}")
    public ResponseEntity<Map<String, Object>> getAllOrdersByShopId(@PathVariable String shopId) {
        try {
            List<Order> orders = orderService.getAllOrdersByShopId(shopId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orders", orders);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting orders for shop {}: {}", shopId, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/update-order-status/{id}")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable String id,
            @RequestBody UpdateOrderStatusRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            // Extract seller ID from token (this would be done by JWT filter in real implementation)
            String sellerId = "seller123"; // This should be extracted from JWT token
            
            Order order = orderService.updateOrderStatus(id, request, sellerId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("order", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating order status: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/order-refund/{id}")
    public ResponseEntity<Map<String, Object>> updateOrderRefund(
            @PathVariable String id,
            @RequestBody UpdateOrderStatusRequest request) {
        try {
            Order order = orderService.updateOrderRefund(id, request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("order", order);
            response.put("message", "Order Refund Request successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing refund: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/order-refund-success/{id}")
    public ResponseEntity<Map<String, Object>> updateOrderRefundSuccess(
            @PathVariable String id,
            @RequestBody UpdateOrderStatusRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            Order order = orderService.updateOrderRefundSuccess(id, request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Order Refund successful!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing refund success: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/admin-all-orders")
    public ResponseEntity<Map<String, Object>> getAllOrdersForAdmin(@RequestHeader("Authorization") String token) {
        try {
            List<Order> orders = orderService.getAllOrdersForAdmin();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orders", orders);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting all orders for admin: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}