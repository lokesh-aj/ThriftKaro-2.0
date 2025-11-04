@echo off
echo ========================================
echo Starting ThriftKaro Microservices
echo ========================================

echo Starting Eureka Server...
start "Eureka Server" cmd /k "cd EurekaServer && mvn spring-boot:run"

timeout /t 10 /nobreak >nul

echo Starting Gateway...
start "Gateway" cmd /k "cd gateway && mvn spring-boot:run"

timeout /t 5 /nobreak >nul

echo Starting User Service...
start "User Service" cmd /k "cd UserService && mvn spring-boot:run"

echo Starting Product Service...
start "Product Service" cmd /k "cd ProductService && mvn spring-boot:run"

echo Starting Shop Service...
start "Shop Service" cmd /k "cd ShopService && mvn spring-boot:run"

echo Starting Cart Service...
start "Cart Service" cmd /k "cd cart && mvn spring-boot:run"

echo Starting Order Service...
start "Order Service" cmd /k "cd order-service && mvn spring-boot:run"

echo Starting Payment Service...
start "Payment Service" cmd /k "cd PaymentService && mvn spring-boot:run"

echo Starting Chat Service...
start "Chat Service" cmd /k "cd chat-service && mvn spring-boot:run"

echo Starting Notification Service...
start "Notification Service" cmd /k "cd notification-service && mvn spring-boot:run"

echo Starting Event Service...
start "Event Service" cmd /k "cd EventService && mvn spring-boot:run"

echo ========================================
echo All services started!
echo Gateway: http://localhost:8089
echo Eureka: http://localhost:8761
echo ========================================
echo.
echo Press any key to exit...
pause >nul
