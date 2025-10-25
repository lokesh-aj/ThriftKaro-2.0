@echo off
echo ========================================
echo Starting EventService
echo ========================================
echo.

cd EventService

echo Cleaning and building EventService...
call mvn clean package -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to build EventService
    pause
    exit /b 1
)

echo.
echo Starting EventService on port 8089...
echo.

start "EventService" cmd /k "mvn spring-boot:run"

echo EventService started successfully!
echo.
pause


