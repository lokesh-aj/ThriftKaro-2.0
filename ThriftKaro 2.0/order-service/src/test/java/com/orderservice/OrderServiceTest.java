package com.orderservice;

import com.orderservice.dto.OrderResponse;
import com.orderservice.dto.PlaceOrderRequest;
import com.orderservice.entities.Order;
import com.orderservice.repository.OrderRepository;
import com.orderservice.service.OrderService;
import com.orderservice.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    public void testPlaceOrder() {
        // Given
        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setProductId(1L);
        request.setQuantity(2);

        String token = "test-token";
        Long userId = 1L;

        Order savedOrder = Order.builder()
                .id(1L)
                .userId(userId)
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .totalPrice(BigDecimal.valueOf(200.00))
                .status("PENDING")
                .orderDate(LocalDateTime.now())
                .build();

        when(jwtUtil.extractUserId(token)).thenReturn(userId);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // When
        OrderResponse result = orderService.placeOrder(request, token);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals(request.getProductId(), result.getProductId());
        assertEquals(request.getQuantity(), result.getQuantity());
        assertEquals("PENDING", result.getStatus());
    }

    @Test
    public void testGetOrderById() {
        // Given
        Long orderId = 1L;
        Order order = Order.builder()
                .id(orderId)
                .userId(1L)
                .productId(1L)
                .quantity(2)
                .totalPrice(BigDecimal.valueOf(200.00))
                .status("PENDING")
                .orderDate(LocalDateTime.now())
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // When
        OrderResponse result = orderService.getOrderById(orderId);

        // Then
        assertNotNull(result);
        assertEquals(orderId, result.getId());
    }
}

