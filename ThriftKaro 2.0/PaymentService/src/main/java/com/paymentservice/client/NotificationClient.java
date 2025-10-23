package com.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "NotificationService")
public interface NotificationClient {
    @PostMapping("/api/v2/notification/send")
    void sendNotification(@RequestBody Map<String, Object> payload);
}




