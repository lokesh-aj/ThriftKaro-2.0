@echo off
echo ===============================================
echo Testing Direct API Connections
echo ===============================================
echo.

echo Testing Shop Service (Port 8091)...
curl -X GET "http://localhost:8091/api/v2/shop/health" -H "Content-Type: application/json" 2>nul
if %errorlevel% equ 0 (
    echo ✓ Shop Service is accessible
) else (
    echo ✗ Shop Service is not accessible
)
echo.

echo Testing User Service (Port 8082)...
curl -X GET "http://localhost:8082/actuator/health" -H "Content-Type: application/json" 2>nul
if %errorlevel% equ 0 (
    echo ✓ User Service is accessible
) else (
    echo ✗ User Service is not accessible
)
echo.

echo Testing Cart Service (Port 8088)...
curl -X GET "http://localhost:8088/actuator/health" -H "Content-Type: application/json" 2>nul
if %errorlevel% equ 0 (
    echo ✓ Cart Service is accessible
) else (
    echo ✗ Cart Service is not accessible
)
echo.

echo Testing Payment Service (Port 8085)...
curl -X GET "http://localhost:8085/api/v2/payment/health" -H "Content-Type: application/json" 2>nul
if %errorlevel% equ 0 (
    echo ✓ Payment Service is accessible
) else (
    echo ✗ Payment Service is not accessible
)
echo.

echo Testing Product Service (Port 8083)...
curl -X GET "http://localhost:8083/actuator/health" -H "Content-Type: application/json" 2>nul
if %errorlevel% equ 0 (
    echo ✓ Product Service is accessible
) else (
    echo ✗ Product Service is not accessible
)
echo.

echo Testing Order Service (Port 8084)...
curl -X GET "http://localhost:8084/actuator/health" -H "Content-Type: application/json" 2>nul
if %errorlevel% equ 0 (
    echo ✓ Order Service is accessible
) else (
    echo ✗ Order Service is not accessible
)
echo.

echo ===============================================
echo Direct API Connection Test Complete
echo ===============================================
echo.
echo If all services show ✓, your direct connections are working!
echo If any show ✗, make sure those services are running.
echo.
pause
