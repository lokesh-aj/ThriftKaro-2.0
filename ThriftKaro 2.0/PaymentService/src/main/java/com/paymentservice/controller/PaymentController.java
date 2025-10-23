package com.paymentservice.controller;

import com.paymentservice.entities.Payment;
import com.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/payment")
public class PaymentController {

    private final PaymentService paymentService;
    
    @Value("${stripe.public.key}")
    private String stripePublicKey;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("PaymentService UP");
    }

    @GetMapping("/stripeapikey")
    public ResponseEntity<Map<String, String>> getStripeApiKey() {
        Map<String, String> response = new HashMap<>();
        response.put("stripeApiKey", stripePublicKey);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/initiate")
    public ResponseEntity<Payment> initiate(@RequestBody Map<String, Object> body) {
        String orderId = (String) body.get("orderId");
        Long userId = Long.valueOf(body.get("userId").toString());
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        return ResponseEntity.ok(paymentService.initiate(orderId, userId, amount));
    }

    @GetMapping("/status/{orderId}")
    public ResponseEntity<Payment> status(@PathVariable String orderId) {
        return ResponseEntity.ok(paymentService.status(orderId));
    }

    @PostMapping("/refund/{orderId}")
    public ResponseEntity<Payment> refund(@PathVariable String orderId) {
        return ResponseEntity.ok(paymentService.refund(orderId));
    }
}



