@echo off
echo Starting Core Services for Testing Signup...
echo.

echo [1/4] Starting Eureka Server (Service Registry)...
start "Eureka Server" cmd /k "cd EurekaServer && mvn spring-boot:run"
timeout /t 20 /nobreak > nul

echo [2/4] Starting API Gateway...
start "API Gateway" cmd /k "cd gateway && mvn spring-boot:run"
timeout /t 15 /nobreak > nul

echo [3/4] Starting User Service...
start "User Service" cmd /k "cd UserService && mvn spring-boot:run"
timeout /t 15 /nobreak > nul

echo.
echo ========================================
echo Core services are starting...
echo ========================================
echo.
echo Service Status URLs:
echo - Eureka Dashboard: http://localhost:8761
echo - API Gateway: http://localhost:8080
echo - User Service: http://localhost:8082
echo.
echo Wait 30-60 seconds for all services to fully start.
echo Then check Eureka Dashboard to verify USERSERVICE is registered.
echo.
echo To test signup, also start the frontend:
echo   cd frontend
echo   npm start
echo.
pause


