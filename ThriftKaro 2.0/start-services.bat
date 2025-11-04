@echo off
echo Starting ThriftKaro Microservices...
echo.

echo Checking Java version...
java -version
echo.

echo Starting Eureka Server...
start "Eureka Server" cmd /k "cd EurekaServer && mvn clean compile spring-boot:run"

timeout /t 15 /nobreak > nul

echo Starting Config Server...
start "Config Server" cmd /k "cd config-server && mvn clean compile spring-boot:run"

timeout /t 10 /nobreak > nul

echo Starting API Gateway...
start "API Gateway" cmd /k "cd gateway && mvn clean compile spring-boot:run"

timeout /t 10 /nobreak > nul

echo Starting User Service...
start "User Service" cmd /k "cd UserService && mvn clean compile spring-boot:run"

timeout /t 10 /nobreak > nul

echo Starting Product Service...
start "Product Service" cmd /k "cd ProductService && mvn clean compile spring-boot:run"

timeout /t 10 /nobreak > nul

echo Starting Cart Service...
start "Cart Service" cmd /k "cd cart && mvn clean compile spring-boot:run"

timeout /t 10 /nobreak > nul

echo Starting Order Service...
start "Order Service" cmd /k "cd order-service && mvn clean compile spring-boot:run"

timeout /t 10 /nobreak > nul

echo Starting Shop Service...
start "Shop Service" cmd /k "cd ShopService && mvn clean compile spring-boot:run"

timeout /t 10 /nobreak > nul

echo Starting Payment Service...
start "Payment Service" cmd /k "cd PaymentService && mvn clean compile spring-boot:run"

timeout /t 10 /nobreak > nul

echo Starting Chat Service...
start "Chat Service" cmd /k "cd chat-service && mvn clean compile spring-boot:run"

timeout /t 10 /nobreak > nul

echo Starting Notification Service...
start "Notification Service" cmd /k "cd notification-service && mvn clean compile spring-boot:run"

timeout /t 10 /nobreak > nul

echo Starting Event Service...
start "Event Service" cmd /k "cd EventService && mvn clean compile spring-boot:run"

echo.
echo All services are starting...
echo.
echo Eureka Server: http://localhost:8761
echo Config Server: http://localhost:8888
echo API Gateway: http://localhost:8089
echo User Service: http://localhost:8082
echo Product Service: http://localhost:8083
echo Cart Service: http://localhost:8088
echo Order Service: http://localhost:8084
echo Shop Service: http://localhost:8091
echo Payment Service: http://localhost:8085
echo Chat Service: http://localhost:8087
echo Notification Service: http://localhost:8086
echo Event Service: http://localhost:8090
echo.
echo Press any key to exit...
pause > nul
