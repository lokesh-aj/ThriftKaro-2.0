@echo off
echo Setting up ThriftKaro Config Repository...

REM Create a temporary directory for the config repository
mkdir temp-config-repo
cd temp-config-repo

REM Initialize git repository
git init

REM Add remote origin
git remote add origin https://github.com/lokesh-aj/thriftkaro-config.git

REM Copy configuration files
copy "..\config-files\*.yml" .
copy "..\config-files\README.md" .

REM Add all files
git add .

REM Commit changes
git commit -m "Initial configuration files for ThriftKaro microservices"

REM Push to GitHub
git branch -M main
git push -u origin main

REM Clean up
cd ..
rmdir /s /q temp-config-repo

echo Configuration files uploaded successfully!
echo You can now start your services in this order:
echo 1. Eureka Server (port 8761)
echo 2. Config Server (port 8888)
echo 3. Other services
echo.
echo Test config server at: http://localhost:8888/order-service/default
pause
