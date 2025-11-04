@echo off
echo Restarting ThriftKaro Microservices with DNS fix...

echo.
echo Stopping all services...
taskkill /f /im java.exe 2>nul

echo.
echo Waiting for services to stop...
timeout /t 3 /nobreak >nul

echo.
echo Starting Eureka Server...
start "Eureka Server" cmd /k "cd /d %~dp0EurekaServer && mvn spring-boot:run"

echo.
echo Waiting for Eureka to start...
timeout /t 10 /nobreak >nul

echo.
echo Starting Config Server...
start "Config Server" cmd /k "cd /d %~dp0config-server && mvn spring-boot:run"

echo.
echo Waiting for Config Server to start...
timeout /t 10 /nobreak >nul

echo.
echo Starting User Service...
start "User Service" cmd /k "cd /d %~dp0UserService && mvn spring-boot:run"

echo.
echo Starting Product Service...
start "Product Service" cmd /k "cd /d %~dp0ProductService && mvn spring-boot:run"

echo.
echo Starting Shop Service...
start "Shop Service" cmd /k "cd /d %~dp0ShopService && mvn spring-boot:run"

echo.
echo Starting Cart Service...
start "Cart Service" cmd /k "cd /d %~dp0cart && mvn spring-boot:run"

echo.
echo Starting Order Service...
start "Order Service" cmd /k "cd /d %~dp0order-service && mvn spring-boot:run"

echo.
echo Starting Payment Service...
start "Payment Service" cmd /k "cd /d %~dp0PaymentService && mvn spring-boot:run"

echo.
echo Starting Chat Service...
start "Chat Service" cmd /k "cd /d %~dp0chat-service && mvn spring-boot:run"

echo.
echo Starting Notification Service...
start "Notification Service" cmd /k "cd /d %~dp0notification-service && mvn spring-boot:run"

echo.
echo Starting Event Service...
start "Event Service" cmd /k "cd /d %~dp0EventService && mvn spring-boot:run"

echo.
echo Waiting for all services to start...
timeout /t 15 /nobreak >nul

echo.
echo Starting Gateway...
start "Gateway" cmd /k "cd /d %~dp0gateway && mvn spring-boot:run"

echo.
echo All services started! Please wait 30 seconds for all services to fully initialize.
echo.
echo Services should be available at:
echo - Gateway: http://localhost:8089
echo - Eureka Dashboard: http://localhost:8761
echo - User Service: http://localhost:8082
echo - Product Service: http://localhost:8083
echo - Order Service: http://localhost:8084
echo - Payment Service: http://localhost:8085
echo - Notification Service: http://localhost:8086
echo - Chat Service: http://localhost:8087
echo - Cart Service: http://localhost:8088
echo - Shop Service: http://localhost:8091
echo - Event Service: http://localhost:8090
echo.
pause
