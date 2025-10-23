package com.orderservice.service;

import com.orderservice.client.NotificationServiceClient;
import com.orderservice.dto.CreateOrderRequest;
import com.orderservice.dto.OrderConfirmationRequest;
import com.orderservice.dto.UpdateOrderStatusRequest;
import com.orderservice.entities.Order;
import com.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final NotificationServiceClient notificationServiceClient;

    public List<Order> createOrder(CreateOrderRequest request) {
        List<Map<String, Object>> cart = request.getCart();
        
        // Group cart items by shopId
        Map<String, List<Map<String, Object>>> shopItemsMap = new HashMap<>();
        
        for (Map<String, Object> item : cart) {
            String shopId = (String) item.get("shopId");
            if (!shopItemsMap.containsKey(shopId)) {
                shopItemsMap.put(shopId, new ArrayList<>());
            }
            shopItemsMap.get(shopId).add(item);
        }
        
        // Create an order for each shop
        List<Order> orders = new ArrayList<>();
        
        for (Map.Entry<String, List<Map<String, Object>>> entry : shopItemsMap.entrySet()) {
            Order order = Order.builder()
                    .cart(entry.getValue())
                    .shippingAddress(request.getShippingAddress())
                    .user(request.getUser())
                    .totalPrice(request.getTotalPrice())
                    .paymentInfo(request.getPaymentInfo())
                    .status("Processing")
                    .paidAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .build();
            
            Order savedOrder = orderRepository.save(order);
            orders.add(savedOrder);
            
            // Send order confirmation email
            try {
                sendOrderConfirmationEmail(savedOrder, request.getUser().get("email").toString());
            } catch (Exception e) {
                log.error("Failed to send order confirmation email for order {}: {}", savedOrder.getId(), e.getMessage());
                // Don't fail the order creation if email fails
            }
        }
        
        return orders;
    }

    public List<Order> getAllOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getAllOrdersByShopId(String shopId) {
        return orderRepository.findByCartShopId(shopId);
    }

    public Order updateOrderStatus(String id, UpdateOrderStatusRequest request, String sellerId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with this id"));
        
        String status = request.getStatus();
        order.setStatus(status);
        
        if ("Transferred to delivery partner".equals(status)) {
            // Update product stock and sold_out
            updateProductStock(order.getCart());
        }
        
        if ("Delivered".equals(status)) {
            order.setDeliveredAt(LocalDateTime.now());
            Map<String, Object> paymentInfo = order.getPaymentInfo();
            if (paymentInfo != null) {
                paymentInfo.put("status", "Succeeded");
            }
            
            // Update seller balance
            updateSellerBalance(order.getTotalPrice(), sellerId);
        }
        
        return orderRepository.save(order);
    }

    public Order updateOrderRefund(String id, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with this id"));
        
        order.setStatus(request.getStatus());
        return orderRepository.save(order);
    }

    public Order updateOrderRefundSuccess(String id, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with this id"));
        
        order.setStatus(request.getStatus());
        
        if ("Refund Success".equals(request.getStatus())) {
            // Restore product stock
            restoreProductStock(order.getCart());
        }
        
        return orderRepository.save(order);
    }

    public List<Order> getAllOrdersForAdmin() {
        return orderRepository.findAllByOrderByDeliveredAtDescCreatedAtDesc();
    }

    private void updateProductStock(List<Map<String, Object>> cart) {
        // This would typically call the Product Service to update stock
        // For now, we'll just log the action
        log.info("Updating product stock for cart items");
        for (Map<String, Object> item : cart) {
            String productId = (String) item.get("_id");
            Integer quantity = (Integer) item.get("qty");
            log.info("Updating stock for product {} with quantity {}", productId, quantity);
        }
    }

    private void restoreProductStock(List<Map<String, Object>> cart) {
        // This would typically call the Product Service to restore stock
        // For now, we'll just log the action
        log.info("Restoring product stock for cart items");
        for (Map<String, Object> item : cart) {
            String productId = (String) item.get("_id");
            Integer quantity = (Integer) item.get("qty");
            log.info("Restoring stock for product {} with quantity {}", productId, quantity);
        }
    }

    private void updateSellerBalance(Double totalPrice, String sellerId) {
        // This would typically call the User Service to update seller balance
        // For now, we'll just log the action
        Double serviceCharge = totalPrice * 0.10;
        Double sellerAmount = totalPrice - serviceCharge;
        log.info("Updating seller {} balance with amount {}", sellerId, sellerAmount);
    }
    
    private void sendOrderConfirmationEmail(Order order, String userEmail) {
        try {
            OrderConfirmationRequest request = OrderConfirmationRequest.builder()
                    .orderId(order.getId())
                    .userEmail(userEmail)
                    .orderDetails(order.getCart())
                    .totalPrice(order.getTotalPrice())
                    .shippingAddress(order.getShippingAddress())
                    .status(order.getStatus())
                    .build();
            
            ResponseEntity<Map<String, Object>> response = notificationServiceClient.sendOrderConfirmation(request);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Order confirmation email sent successfully for order ID: {}", order.getId());
            } else {
                log.warn("Failed to send order confirmation email for order ID: {}. Response: {}", 
                        order.getId(), response.getBody());
            }
        } catch (Exception e) {
            log.error("Exception occurred while sending order confirmation email for order ID: {}. Error: {}", 
                    order.getId(), e.getMessage());
            throw e;
        }
    }
}