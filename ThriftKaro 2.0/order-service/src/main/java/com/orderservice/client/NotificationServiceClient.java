package com.orderservice.client;

import com.orderservice.dto.OrderConfirmationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "NotificationService", url = "http://localhost:8086")
public interface NotificationServiceClient {
    
    @PostMapping("/api/v2/notification/send-order-confirmation")
    ResponseEntity<Map<String, Object>> sendOrderConfirmation(@RequestBody OrderConfirmationRequest request);
}
