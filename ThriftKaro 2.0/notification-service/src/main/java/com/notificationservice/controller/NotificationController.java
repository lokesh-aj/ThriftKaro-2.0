package com.notificationservice.controller;

import com.notificationservice.dto.EmailRequest;
import com.notificationservice.dto.OrderConfirmationRequest;
import com.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/notification")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/send-email")
    public ResponseEntity<Map<String, Object>> sendEmail(@RequestBody EmailRequest request) {
        try {
            emailService.sendEmail(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Email sent successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/send-withdraw-request")
    public ResponseEntity<Map<String, Object>> sendWithdrawRequestEmail(
            @RequestParam String email,
            @RequestParam String sellerName,
            @RequestParam Double amount) {
        try {
            emailService.sendWithdrawRequestEmail(email, sellerName, amount);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Withdraw request email sent successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending withdraw request email: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/send-payment-confirmation")
    public ResponseEntity<Map<String, Object>> sendPaymentConfirmationEmail(
            @RequestParam String email,
            @RequestParam String sellerName,
            @RequestParam Double amount) {
        try {
            emailService.sendPaymentConfirmationEmail(email, sellerName, amount);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment confirmation email sent successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending payment confirmation email: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/send-order-confirmation")
    public ResponseEntity<Map<String, Object>> sendOrderConfirmationEmail(@RequestBody OrderConfirmationRequest request) {
        try {
            emailService.sendOrderConfirmationEmail(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Order confirmation email sent successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending order confirmation email: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}

