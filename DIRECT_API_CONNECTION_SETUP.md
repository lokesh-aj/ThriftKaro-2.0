# Direct API Connection Setup - ThriftKaro 2.0

## Overview
This document explains the direct API connection setup that bypasses the API Gateway, allowing the frontend to connect directly to individual microservices.

## Why Direct Connections?
- **Eliminates Gateway Bottleneck**: Removes the single point of failure at the API Gateway
- **Reduces Network Latency**: Direct connections have fewer network hops
- **Simplifies Debugging**: Easier to trace issues to specific services
- **Better Performance**: No gateway processing overhead

## Service Ports and Endpoints

| Service | Port | Health Check | Main Endpoints |
|---------|------|--------------|----------------|
| User Service | 8082 | `/actuator/health` | `/api/v2/user/**` |
| Shop Service | 8089 | `/api/v2/shop/health` | `/api/v2/shop/**` |
| Product Service | 8083 | `/actuator/health` | `/api/v2/product/**` |
| Cart Service | 8088 | `/actuator/health` | `/api/v2/cart/**` |
| Order Service | 8084 | `/actuator/health` | `/api/v2/order/**` |
| Payment Service | 8085 | `/api/v2/payment/health` | `/api/v2/payment/**` |
| Chat Service | 8087 | `/actuator/health` | `/api/v2/conversation/**`, `/api/v2/message/**` |
| Notification Service | 8086 | `/actuator/health` | `/api/v2/notification/**` |
| Event Service | 8090 | `/actuator/health` | `/api/v2/event/**` |

## Frontend Configuration Changes

### 1. New Direct API Instances
Created `frontend/src/api/directApiInstances.js` with individual axios instances for each service:

```javascript
export const userApiInstance = axios.create({
  baseURL: "http://localhost:8082", // User Service
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
});

export const shopApiInstance = axios.create({
  baseURL: "http://localhost:8089", // Shop Service
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
});
// ... and so on for all services
```

### 2. Updated Existing API Files
- **axiosInstance.js**: Now connects directly to User Service (port 8082)
- **shopAxiosInstance.js**: Now connects directly to Shop Service (port 8089)
- **cartService.js**: Now uses direct Cart Service connection (port 8088)
- **paymentService.js**: Now uses direct Payment Service connection (port 8085)

### 3. Updated Components
- **ShopLogin.jsx**: Now uses `shopApiInstance` from directApiInstances

## Backend CORS Configuration

### Updated Services
Added CORS configuration to services that didn't have it:

1. **Shop Service** (`ShopService/src/main/java/com/shopservice/security/SecurityConfig.java`)
2. **Cart Service** (`cart/src/main/java/com/cart/security/SecurityConfig.java`)
3. **Payment Service** (`PaymentService/src/main/java/com/paymentservice/security/SecurityConfig.java`)

### CORS Configuration Pattern
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(Arrays.asList("*"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

## Testing Direct Connections

### 1. Run the Test Script
```bash
cd "ThriftKaro 2.0"
test-direct-connections.bat
```

### 2. Manual Testing
Test individual services using curl or Postman:

```bash
# Test Shop Service
curl -X GET "http://localhost:8089/api/v2/shop/health"

# Test Shop Login (example)
curl -X POST "http://localhost:8089/api/v2/shop/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

## How to Start Services

### 1. Start All Microservices
```bash
cd "ThriftKaro 2.0"
start-services.bat
```

### 2. Wait for Services to Register
Wait 2-3 minutes for all services to register with Eureka.

### 3. Test Connections
```bash
test-direct-connections.bat
```

### 4. Start Frontend
```bash
cd frontend
npm start
```

## Troubleshooting

### Common Issues

1. **CORS Errors**
   - Ensure all services have CORS configuration
   - Check that `setAllowCredentials(true)` is set
   - Verify `setAllowedOriginPatterns(Arrays.asList("*"))` is configured

2. **Service Not Accessible**
   - Check if service is running on correct port
   - Verify service health endpoint
   - Check firewall settings

3. **Authentication Errors**
   - Ensure JWT tokens are being sent in headers
   - Verify JWT secret is same across all services
   - Check token expiration

4. **Network Errors**
   - Verify service is running
   - Check port conflicts
   - Ensure MongoDB Atlas connection is working

### Debug Steps

1. **Check Service Status**
   ```bash
   netstat -ano | findstr :8089  # Check if port is in use
   ```

2. **Check Service Logs**
   - Look at the command prompt window for each service
   - Check for startup errors or connection issues

3. **Test Individual Endpoints**
   - Use the test script or manual curl commands
   - Verify each service responds correctly

## Migration from Gateway

### What Changed
- Frontend now connects directly to individual services
- API Gateway is no longer required for frontend connections
- Each service handles its own CORS configuration

### What Stayed the Same
- API endpoints remain the same (`/api/v2/shop/login`, etc.)
- JWT authentication still works
- Database connections unchanged
- Service-to-service communication still uses Eureka

## Benefits Achieved

1. **Eliminated Gateway Dependency**: Frontend no longer depends on API Gateway
2. **Improved Performance**: Direct connections reduce latency
3. **Better Error Handling**: Easier to identify which service has issues
4. **Simplified Architecture**: Fewer moving parts in the request path
5. **Enhanced Debugging**: Direct service logs are easier to trace

## Next Steps

1. **Monitor Performance**: Compare response times before and after
2. **Update Documentation**: Update API documentation to reflect direct connections
3. **Consider Load Balancing**: If needed, implement client-side load balancing
4. **Security Review**: Ensure direct connections don't compromise security

---

**Note**: The API Gateway can still be used for external API access or service-to-service communication if needed, but the frontend now bypasses it entirely.
