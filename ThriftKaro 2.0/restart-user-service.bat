@echo off
echo Restarting UserService with CORS fix...

echo.
echo Stopping UserService...
taskkill /f /im java.exe /fi "WINDOWTITLE eq UserService*" 2>nul

echo.
echo Waiting for UserService to stop...
timeout /t 3 /nobreak >nul

echo.
echo Starting UserService...
start "UserService" cmd /k "cd /d %~dp0UserService && mvn spring-boot:run"

echo.
echo UserService restarted! Please wait 10 seconds for it to fully initialize.
echo.
pause
