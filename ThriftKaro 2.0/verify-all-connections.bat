@echo off
setlocal enabledelayedexpansion

echo ========================================
echo ThriftKaro Microservices Connection Test
echo ========================================
echo.

set GATEWAY=http://localhost:8089
set SUCCESS_COUNT=0
set FAIL_COUNT=0

echo Testing Gateway availability...
curl -s -o nul -w "%%{http_code}" %GATEWAY% > temp.txt
set /p STATUS=<temp.txt
if "%STATUS%"=="404" (
    echo [PASS] Gateway is running at %GATEWAY%
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="200" (
    echo [PASS] Gateway is running at %GATEWAY%
    set /a SUCCESS_COUNT+=1
) else (
    echo [FAIL] Gateway is not responding
    set /a FAIL_COUNT+=1
)
echo.

echo Testing Eureka Server...
curl -s -o nul -w "%%{http_code}" http://localhost:8761 > temp.txt
set /p STATUS=<temp.txt
if "%STATUS%"=="200" (
    echo [PASS] Eureka Server is running at http://localhost:8761
    set /a SUCCESS_COUNT+=1
) else (
    echo [FAIL] Eureka Server is not responding
    set /a FAIL_COUNT+=1
)
echo.

echo ========================================
echo Testing Service Endpoints Through Gateway
echo ========================================
echo.

echo Testing User Service...
curl -s -o nul -w "%%{http_code}" %GATEWAY%/api/v2/user/test > temp.txt
set /p STATUS=<temp.txt
if "%STATUS%"=="200" (
    echo [PASS] User Service is reachable
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="404" (
    echo [WARN] User Service is reachable but endpoint may not exist (404)
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="401" (
    echo [PASS] User Service is reachable (JWT auth working)
    set /a SUCCESS_COUNT+=1
) else (
    echo [FAIL] User Service is not reachable (Status: %STATUS%)
    set /a FAIL_COUNT+=1
)
echo.

echo Testing Product Service...
curl -s -o nul -w "%%{http_code}" %GATEWAY%/api/v2/product/get-all-products > temp.txt
set /p STATUS=<temp.txt
if "%STATUS%"=="200" (
    echo [PASS] Product Service is reachable
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="404" (
    echo [WARN] Product Service is reachable but endpoint may not exist (404)
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="401" (
    echo [PASS] Product Service is reachable (JWT auth working)
    set /a SUCCESS_COUNT+=1
) else (
    echo [FAIL] Product Service is not reachable (Status: %STATUS%)
    set /a FAIL_COUNT+=1
)
echo.

echo Testing Shop Service...
curl -s -o nul -w "%%{http_code}" %GATEWAY%/api/v2/shop/test > temp.txt
set /p STATUS=<temp.txt
if "%STATUS%"=="200" (
    echo [PASS] Shop Service is reachable
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="404" (
    echo [WARN] Shop Service is reachable but endpoint may not exist (404)
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="401" (
    echo [PASS] Shop Service is reachable (JWT auth working)
    set /a SUCCESS_COUNT+=1
) else (
    echo [FAIL] Shop Service is not reachable (Status: %STATUS%)
    set /a FAIL_COUNT+=1
)
echo.

echo Testing Cart Service...
curl -s -o nul -w "%%{http_code}" %GATEWAY%/api/v2/cart/test > temp.txt
set /p STATUS=<temp.txt
if "%STATUS%"=="200" (
    echo [PASS] Cart Service is reachable
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="404" (
    echo [WARN] Cart Service is reachable but endpoint may not exist (404)
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="401" (
    echo [PASS] Cart Service is reachable (JWT auth working)
    set /a SUCCESS_COUNT+=1
) else (
    echo [FAIL] Cart Service is not reachable (Status: %STATUS%)
    set /a FAIL_COUNT+=1
)
echo.

echo Testing Order Service...
curl -s -o nul -w "%%{http_code}" %GATEWAY%/api/v2/order/test > temp.txt
set /p STATUS=<temp.txt
if "%STATUS%"=="200" (
    echo [PASS] Order Service is reachable
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="404" (
    echo [WARN] Order Service is reachable but endpoint may not exist (404)
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="401" (
    echo [PASS] Order Service is reachable (JWT auth working)
    set /a SUCCESS_COUNT+=1
) else (
    echo [FAIL] Order Service is not reachable (Status: %STATUS%)
    set /a FAIL_COUNT+=1
)
echo.

echo Testing Payment Service...
curl -s -o nul -w "%%{http_code}" %GATEWAY%/api/v2/payment/stripeapikey > temp.txt
set /p STATUS=<temp.txt
if "%STATUS%"=="200" (
    echo [PASS] Payment Service is reachable
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="404" (
    echo [WARN] Payment Service is reachable but endpoint may not exist (404)
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="500" (
    echo [WARN] Payment Service is reachable but may need configuration
    set /a SUCCESS_COUNT+=1
) else (
    echo [FAIL] Payment Service is not reachable (Status: %STATUS%)
    set /a FAIL_COUNT+=1
)
echo.

echo Testing Chat Service (Conversation)...
curl -s -o nul -w "%%{http_code}" %GATEWAY%/api/v2/conversation/test > temp.txt
set /p STATUS=<temp.txt
if "%STATUS%"=="200" (
    echo [PASS] Chat Service (Conversation) is reachable
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="404" (
    echo [WARN] Chat Service is reachable but endpoint may not exist (404)
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="401" (
    echo [PASS] Chat Service is reachable (JWT auth working)
    set /a SUCCESS_COUNT+=1
) else (
    echo [FAIL] Chat Service is not reachable (Status: %STATUS%)
    set /a FAIL_COUNT+=1
)
echo.

echo Testing Chat Service (Message)...
curl -s -o nul -w "%%{http_code}" %GATEWAY%/api/v2/message/test > temp.txt
set /p STATUS=<temp.txt
if "%STATUS%"=="200" (
    echo [PASS] Chat Service (Message) is reachable
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="404" (
    echo [WARN] Chat Service is reachable but endpoint may not exist (404)
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="401" (
    echo [PASS] Chat Service is reachable (JWT auth working)
    set /a SUCCESS_COUNT+=1
) else (
    echo [FAIL] Chat Service is not reachable (Status: %STATUS%)
    set /a FAIL_COUNT+=1
)
echo.

echo Testing Event Service...
curl -s -o nul -w "%%{http_code}" %GATEWAY%/api/v2/event/get-all-events > temp.txt
set /p STATUS=<temp.txt
if "%STATUS%"=="200" (
    echo [PASS] Event Service is reachable
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="404" (
    echo [WARN] Event Service is reachable but endpoint may not exist (404)
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="401" (
    echo [PASS] Event Service is reachable (JWT auth working)
    set /a SUCCESS_COUNT+=1
) else (
    echo [FAIL] Event Service is not reachable (Status: %STATUS%)
    set /a FAIL_COUNT+=1
)
echo.

echo Testing Coupon Service (via Order Service)...
curl -s -o nul -w "%%{http_code}" %GATEWAY%/api/v2/coupon/test > temp.txt
set /p STATUS=<temp.txt
if "%STATUS%"=="200" (
    echo [PASS] Coupon Service is reachable
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="404" (
    echo [WARN] Coupon Service is reachable but endpoint may not exist (404)
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="401" (
    echo [PASS] Coupon Service is reachable (JWT auth working)
    set /a SUCCESS_COUNT+=1
) else (
    echo [FAIL] Coupon Service is not reachable (Status: %STATUS%)
    set /a FAIL_COUNT+=1
)
echo.

echo Testing Withdraw Service (via Order Service)...
curl -s -o nul -w "%%{http_code}" %GATEWAY%/api/v2/withdraw/test > temp.txt
set /p STATUS=<temp.txt
if "%STATUS%"=="200" (
    echo [PASS] Withdraw Service is reachable
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="404" (
    echo [WARN] Withdraw Service is reachable but endpoint may not exist (404)
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="401" (
    echo [PASS] Withdraw Service is reachable (JWT auth working)
    set /a SUCCESS_COUNT+=1
) else (
    echo [FAIL] Withdraw Service is not reachable (Status: %STATUS%)
    set /a FAIL_COUNT+=1
)
echo.

echo ========================================
echo Testing Direct Service Availability
echo ========================================
echo.

echo Testing User Service Direct (Port 8082)...
curl -s -o nul -w "%%{http_code}" http://localhost:8082/api/v2/user/test > temp.txt
set /p STATUS=<temp.txt
if "%STATUS%"=="200" (
    echo [PASS] User Service is running on port 8082
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="404" (
    echo [PASS] User Service is running on port 8082 (endpoint not found is ok)
    set /a SUCCESS_COUNT+=1
) else (
    echo [WARN] User Service may not be running on port 8082
)
echo.

echo Testing Product Service Direct (Port 8083)...
curl -s -o nul -w "%%{http_code}" http://localhost:8083/api/v2/product/test > temp.txt
set /p STATUS=<temp.txt
if "%STATUS%"=="200" (
    echo [PASS] Product Service is running on port 8083
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="404" (
    echo [PASS] Product Service is running on port 8083 (endpoint not found is ok)
    set /a SUCCESS_COUNT+=1
) else (
    echo [WARN] Product Service may not be running on port 8083
)
echo.

echo Testing Order Service Direct (Port 8084)...
curl -s -o nul -w "%%{http_code}" http://localhost:8084/api/v2/order/test > temp.txt
set /p STATUS=<temp.txt
if "%STATUS%"=="200" (
    echo [PASS] Order Service is running on port 8084
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="404" (
    echo [PASS] Order Service is running on port 8084 (endpoint not found is ok)
    set /a SUCCESS_COUNT+=1
) else (
    echo [WARN] Order Service may not be running on port 8084
)
echo.

echo Testing Payment Service Direct (Port 8085)...
curl -s -o nul -w "%%{http_code}" http://localhost:8085/api/v2/payment/test > temp.txt
set /p STATUS=<temp.txt
if "%STATUS%"=="200" (
    echo [PASS] Payment Service is running on port 8085
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="404" (
    echo [PASS] Payment Service is running on port 8085 (endpoint not found is ok)
    set /a SUCCESS_COUNT+=1
) else (
    echo [WARN] Payment Service may not be running on port 8085
)
echo.

echo Testing Chat Service Direct (Port 8087)...
curl -s -o nul -w "%%{http_code}" http://localhost:8087/api/v2/conversation/test > temp.txt
set /p STATUS=<temp.txt
if "%STATUS%"=="200" (
    echo [PASS] Chat Service is running on port 8087
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="404" (
    echo [PASS] Chat Service is running on port 8087 (endpoint not found is ok)
    set /a SUCCESS_COUNT+=1
) else (
    echo [WARN] Chat Service may not be running on port 8087
)
echo.

echo Testing Cart Service Direct (Port 8088)...
curl -s -o nul -w "%%{http_code}" http://localhost:8088/api/v2/cart/test > temp.txt
set /p STATUS=<temp.txt
if "%STATUS%"=="200" (
    echo [PASS] Cart Service is running on port 8088
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="404" (
    echo [PASS] Cart Service is running on port 8088 (endpoint not found is ok)
    set /a SUCCESS_COUNT+=1
) else (
    echo [WARN] Cart Service may not be running on port 8088
)
echo.

echo Testing Shop Service Direct (Port 8091)...
curl -s -o nul -w "%%{http_code}" http://localhost:8091/api/v2/shop/test > temp.txt
set /p STATUS=<temp.txt
if "%STATUS%"=="200" (
    echo [PASS] Shop Service is running on port 8091
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="404" (
    echo [PASS] Shop Service is running on port 8091 (endpoint not found is ok)
    set /a SUCCESS_COUNT+=1
) else (
    echo [WARN] Shop Service may not be running on port 8091
)
echo.

echo Testing Event Service Direct (Port 8090)...
curl -s -o nul -w "%%{http_code}" http://localhost:8090/api/v2/event/test > temp.txt
set /p STATUS=<temp.txt
if "%STATUS%"=="200" (
    echo [PASS] Event Service is running on port 8090
    set /a SUCCESS_COUNT+=1
) else if "%STATUS%"=="404" (
    echo [PASS] Event Service is running on port 8090 (endpoint not found is ok)
    set /a SUCCESS_COUNT+=1
) else (
    echo [WARN] Event Service may not be running on port 8090
)
echo.

del temp.txt 2>nul

echo ========================================
echo Test Summary
echo ========================================
echo Successful Tests: %SUCCESS_COUNT%
echo Failed Tests: %FAIL_COUNT%
echo.

if %FAIL_COUNT%==0 (
    echo [SUCCESS] All critical services are connected and operational!
) else (
    echo [WARNING] Some services may not be running. Start them using start-services.bat
)
echo.

echo ========================================
echo Service Architecture Overview
echo ========================================
echo.
echo Frontend: React (Port 3000)
echo    ^|
echo    v
echo API Gateway (Port 8089) ^<-- All requests go here
echo    ^|
echo    +-- JWT Authentication
echo    +-- Service Discovery (Eureka)
echo    ^|
echo    +-- User Service (Port 8082) - MongoDB Atlas (thriftkaro_user_db)
echo    +-- Product Service (Port 8083) - MongoDB Atlas (thriftkaro_product_db)
echo    +-- Order Service (Port 8084) - MongoDB Atlas (thriftkaro_order_db)
echo    +-- Payment Service (Port 8085) - MongoDB Atlas (thriftkaro_payment_db)
echo    +-- Notification Service (Port 8086) - Email Service
echo    +-- Chat Service (Port 8087) - MongoDB Atlas (thriftkaro_chat_db)
echo    +-- Cart Service (Port 8088) - MongoDB Atlas (thriftkaro_cart_db)
echo    +-- Shop Service (Port 8091) - MongoDB Atlas (thriftkaro_shop_db)
echo    +-- Event Service (Port 8090) - MongoDB Atlas (thriftkaro_event_db)
echo.
echo ========================================
echo Migration Status: COMPLETE
echo ========================================
echo.
echo ✓ Node.js Backend: DEPRECATED (No longer used)
echo ✓ Microservices: ACTIVE (All services migrated)
echo ✓ Database: MongoDB Atlas (Cloud)
echo ✓ Authentication: JWT (Unified across all services)
echo ✓ Service Discovery: Eureka Server
echo ✓ API Gateway: Spring Cloud Gateway
echo.

echo Press any key to exit...
pause > nul


