# ThriftKaro Configuration Repository

This repository contains centralized configuration files for all ThriftKaro microservices.

## Services Configuration

- `application.yml` - Default configuration for all services
- `order-service.yml` - Order Service specific configuration
- `user-service.yml` - User Service specific configuration  
- `product-service.yml` - Product Service specific configuration
- `shop-service.yml` - Shop Service specific configuration
- `payment-service.yml` - Payment Service specific configuration
- `notification-service.yml` - Notification Service specific configuration
- `chat-service.yml` - Chat Service specific configuration
- `cart-service.yml` - Cart Service specific configuration

## Setup Instructions

1. **Database Setup**: Create the following MySQL databases:
   - `thriftkaro` (default)
   - `thriftkaro_orders`
   - `thriftkaro_users`
   - `thriftkaro_products`
   - `thriftkaro_shops`
   - `thriftkaro_payments`
   - `thriftkaro_notifications`
   - `thriftkaro_chat`
   - `thriftkaro_cart`

2. **Redis Setup**: Install and start Redis server on localhost:6379

3. **Service Ports**:
   - Config Server: 8888
   - Eureka Server: 8761
   - Order Service: 8081
   - User Service: 8082
   - Product Service: 8083
   - Shop Service: 8084
   - Payment Service: 8085
   - Notification Service: 8086
   - Chat Service: 8087
   - Cart Service: 8088

## Environment Variables

Update the following in your configuration files:
- Database passwords
- Email credentials
- Stripe API keys
- JWT secrets

## Testing Configuration

Visit `http://localhost:8888/{service-name}/default` to test configuration loading.
