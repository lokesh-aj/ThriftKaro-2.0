package com.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "OrderService")
public interface OrderClient {
    @GetMapping("/api/v2/order/details/{orderId}")
    Map<String, Object> getOrderDetails(@PathVariable("orderId") String orderId);
}




