@echo off
echo Restarting Gateway with JWT fix...

echo.
echo Stopping Gateway...
taskkill /f /im java.exe /fi "WINDOWTITLE eq Gateway*" 2>nul

echo.
echo Waiting for Gateway to stop...
timeout /t 3 /nobreak >nul

echo.
echo Starting Gateway...
start "Gateway" cmd /k "cd /d %~dp0gateway && mvn spring-boot:run"

echo.
echo Gateway restarted! Please wait 10 seconds for it to fully initialize.
echo.
pause
