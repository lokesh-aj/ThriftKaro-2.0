package com.paymentservice.service;

import com.paymentservice.client.NotificationClient;
import com.paymentservice.entities.Payment;
import com.paymentservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final NotificationClient notificationClient;

    public PaymentService(PaymentRepository paymentRepository, NotificationClient notificationClient) {
        this.paymentRepository = paymentRepository;
        this.notificationClient = notificationClient;
    }

    public Payment initiate(String orderId, Long userId, BigDecimal amount) {
        Payment payment = Payment.builder()
                .orderId(orderId)
                .userId(userId.toString())
                .amount(amount)
                .status("INITIATED")
                .timestamp(LocalDateTime.now())
                .build();
        Payment saved = paymentRepository.save(payment);

        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "PAYMENT_INITIATED");
        payload.put("orderId", orderId);
        payload.put("userId", userId);
        payload.put("amount", amount);
        notificationClient.sendNotification(payload);
        return saved;
    }

    public Payment markSuccess(String orderId) {
        Payment latest = paymentRepository.findTopByOrderIdOrderByTimestampDesc(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        latest.setStatus("SUCCESS");
        latest.setTimestamp(LocalDateTime.now());
        Payment saved = paymentRepository.save(latest);

        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "PAYMENT_SUCCESS");
        payload.put("orderId", orderId);
        payload.put("userId", latest.getUserId());
        payload.put("amount", latest.getAmount());
        notificationClient.sendNotification(payload);
        return saved;
    }

    public Payment refund(String orderId) {
        Payment latest = paymentRepository.findTopByOrderIdOrderByTimestampDesc(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        latest.setStatus("REFUNDED");
        latest.setTimestamp(LocalDateTime.now());
        Payment saved = paymentRepository.save(latest);

        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "PAYMENT_REFUNDED");
        payload.put("orderId", orderId);
        payload.put("userId", latest.getUserId());
        payload.put("amount", latest.getAmount());
        notificationClient.sendNotification(payload);
        return saved;
    }

    public Payment status(String orderId) {
        return paymentRepository.findTopByOrderIdOrderByTimestampDesc(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }
}




