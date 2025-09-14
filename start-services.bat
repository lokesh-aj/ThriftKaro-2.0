@echo off
echo Starting ThriftKaro Microservices...
echo.

echo Checking Java version...
java -version
echo.

echo Starting Eureka Server...
start "Eureka Server" cmd /k "cd EurekaServer && mvn clean compile spring-boot:run"

timeout /t 15 /nobreak > nul

echo Starting User Service...
start "User Service" cmd /k "cd UserService && mvn clean compile spring-boot:run"

timeout /t 15 /nobreak > nul

echo Starting Product Service...
start "Product Service" cmd /k "cd ProductService && mvn clean compile spring-boot:run"

timeout /t 15 /nobreak > nul

echo Starting Cart Service...
start "Cart Service" cmd /k "cd cart && mvn clean compile spring-boot:run"

echo.
echo All services are starting...
echo.
echo Eureka Server: http://localhost:8761
echo User Service: http://localhost:8080
echo Product Service: http://localhost:8081
echo Cart Service: http://localhost:8083
echo.
echo Press any key to exit...
pause > nul
