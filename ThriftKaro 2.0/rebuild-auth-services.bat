@echo off
echo ========================================
echo Rebuilding Authentication Services
echo ========================================
echo.

echo [1/3] Rebuilding UserService...
cd UserService
call mvn clean install -DskipTests
if errorlevel 1 (
    echo ERROR: Failed to build UserService
    pause
    exit /b 1
)
cd ..
echo UserService rebuilt successfully!
echo.

echo [2/3] Rebuilding ShopService...
cd ShopService
call mvn clean install -DskipTests
if errorlevel 1 (
    echo ERROR: Failed to build ShopService
    pause
    exit /b 1
)
cd ..
echo ShopService rebuilt successfully!
echo.

echo [3/3] Rebuilding Gateway...
cd gateway
call mvn clean install -DskipTests
if errorlevel 1 (
    echo ERROR: Failed to build Gateway
    pause
    exit /b 1
)
cd ..
echo Gateway rebuilt successfully!
echo.

echo ========================================
echo All authentication services rebuilt successfully!
echo ========================================
echo.
echo Next steps:
echo 1. Restart Gateway (port 8080)
echo 2. Restart UserService
echo 3. Restart ShopService
echo 4. Test the authentication flows
echo.
pause


