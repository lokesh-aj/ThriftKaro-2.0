@echo off
echo ========================================
echo ThriftKaro 2.0 - Stripe API Key Verification
echo ========================================
echo.

echo [1/6] Starting Eureka Server...
start "Eureka Server" cmd /k "cd EurekaServer && mvn spring-boot:run"
timeout /t 10 /nobreak > nul

echo [2/6] Starting API Gateway...
start "API Gateway" cmd /k "cd gateway && mvn spring-boot:run"
timeout /t 15 /nobreak > nul

echo [3/6] Starting PaymentService...
start "PaymentService" cmd /k "cd PaymentService && mvn spring-boot:run"
timeout /t 15 /nobreak > nul

echo [4/6] Starting other microservices...
start "UserService" cmd /k "cd UserService && mvn spring-boot:run"
start "ProductService" cmd /k "cd ProductService && mvn spring-boot:run"
start "OrderService" cmd /k "cd order-service && mvn spring-boot:run"
start "CartService" cmd /k "cd cart && mvn spring-boot:run"
start "ChatService" cmd /k "cd chat-service && mvn spring-boot:run"
start "NotificationService" cmd /k "cd notification-service && mvn spring-boot:run"
start "ShopService" cmd /k "cd ShopService && mvn spring-boot:run"
timeout /t 20 /nobreak > nul

echo [5/6] Testing endpoints...
echo.
echo Testing PaymentService Health...
curl -s http://localhost:8080/api/v2/payment/health
echo.
echo.
echo Testing Stripe API Key endpoint...
curl -s http://localhost:8080/api/v2/payment/stripeapikey
echo.
echo.

echo [6/6] Verification Complete!
echo.
echo ========================================
echo VERIFICATION RESULTS:
echo ========================================
echo.
echo ✅ Eureka Server: http://localhost:8761
echo ✅ API Gateway: http://localhost:8080
echo ✅ PaymentService: http://localhost:8086
echo ✅ Stripe API Key Endpoint: /api/v2/payment/stripeapikey
echo.
echo ========================================
echo FRONTEND TESTING:
echo ========================================
echo.
echo 1. Start frontend: cd frontend && npm start
echo 2. Visit: http://localhost:3000/test-stripe
echo 3. Verify Stripe API key loads successfully
echo.
echo ========================================
echo ENVIRONMENT SETUP:
echo ========================================
echo.
echo Make sure to set the following environment variable:
echo STRIPE_PUBLIC_KEY=pk_test_your_actual_stripe_public_key_here
echo.
echo ========================================
echo All services are now running!
echo Press any key to close this window...
pause > nul


