@echo off
echo Rebuilding User Service...
cd UserService
mvn clean compile
echo.
echo User Service rebuilt successfully!
echo You can now start it with: mvn spring-boot:run
echo.
pause


