@echo off
echo ========================================
echo Testing ThriftKaro Microservices
echo ========================================

echo Testing Gateway...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8089/actuator/health' -Method GET -TimeoutSec 5; Write-Host '[PASS] Gateway is running' } catch { Write-Host '[FAIL] Gateway is not responding' }"

echo Testing Eureka Server...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8761' -Method GET -TimeoutSec 5; Write-Host '[PASS] Eureka Server is running' } catch { Write-Host '[FAIL] Eureka Server is not responding' }"

echo.
echo Testing Service Endpoints...
echo.

echo Testing User Service...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8089/api/v2/user/health' -Method GET -TimeoutSec 5; Write-Host '[PASS] User Service is reachable' } catch { Write-Host '[FAIL] User Service is not reachable' }"

echo Testing Product Service...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8089/api/v2/product/health' -Method GET -TimeoutSec 5; Write-Host '[PASS] Product Service is reachable' } catch { Write-Host '[FAIL] Product Service is not reachable' }"

echo Testing Shop Service...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8091/api/v2/shop/health' -Method GET -TimeoutSec 5; Write-Host '[PASS] Shop Service is reachable' } catch { Write-Host '[FAIL] Shop Service is not reachable' }"

echo Testing Cart Service...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8089/api/v2/cart/health' -Method GET -TimeoutSec 5; Write-Host '[PASS] Cart Service is reachable' } catch { Write-Host '[FAIL] Cart Service is not reachable' }"

echo Testing Order Service...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8089/api/v2/order/health' -Method GET -TimeoutSec 5; Write-Host '[PASS] Order Service is reachable' } catch { Write-Host '[FAIL] Order Service is not reachable' }"

echo Testing Payment Service...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8089/api/v2/payment/health' -Method GET -TimeoutSec 5; Write-Host '[PASS] Payment Service is reachable' } catch { Write-Host '[FAIL] Payment Service is not reachable' }"

echo.
echo ========================================
echo Connection test completed!
echo ========================================
pause
