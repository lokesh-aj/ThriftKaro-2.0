# Comprehensive Frontend-Backend Connection Audit

**Date**: October 25, 2025  
**Status**: 🔍 Audit Complete - Issues Found

## Executive Summary

This document provides a comprehensive audit of all frontend-backend connections in the ThriftKaro project. Several critical mismatches have been identified that will prevent successful operation.

---

## ✅ WORKING Services

### 1. UserService (Authentication)
**Status**: ✅ FIXED (Previously)
- **Backend Path**: `/api/auth/register`, `/api/auth/login`, `/api/auth/me`
- **Frontend Calls**: `/api/v2/user/create-user`, `/api/v2/user/login-user`, `/api/v2/user/getuser`
- **Gateway Routing**: ✅ Properly mapped with compatibility rewrites
- **Response Format**: ✅ Consistent (includes token + user data)

### 2. ShopService (Authentication)
**Status**: ✅ FIXED (Previously)
- **Backend Path**: `/api/v2/shop/register`, `/api/v2/shop/login`
- **Frontend Calls**: `/api/v2/shop/create-shop`, `/api/v2/shop/login-shop`
- **Gateway Routing**: ✅ Properly mapped with compatibility rewrites
- **Response Format**: ✅ Consistent (includes token + seller data)

### 3. OrderService
**Status**: ✅ WORKING
- **Backend Path**: `/api/v2/order/**`
- **Frontend Calls**: 
  - `/api/v2/order/create-order`
  - `/api/v2/order/get-all-orders/{userId}`
  - `/api/v2/order/get-seller-all-orders/{shopId}`
  - `/api/v2/order/admin-all-orders`
- **Gateway Routing**: ✅ Mapped to OrderService
- **Issues**: None identified

### 4. PaymentService
**Status**: ✅ WORKING
- **Backend Path**: `/api/v2/payment/**`
- **Frontend Calls**:
  - `/api/v2/payment/stripeapikey`
  - `/api/v2/payment/initiate`
  - `/api/v2/payment/status/{orderId}`
  - `/api/v2/payment/refund/{orderId}`
- **Gateway Routing**: ✅ Mapped to PaymentService
- **Issues**: None identified (Note: Stripe public key needs to be configured)

### 5. ChatService (Conversation & Messages)
**Status**: ✅ WORKING
- **Backend Path**: `/api/v2/conversation/**`, `/api/v2/message/**`
- **Gateway Routing**: ✅ Mapped to ChatService
- **WebSocket**: ✅ Configured at `/ws/**`
- **Issues**: None identified

---

## ❌ BROKEN Services

### 1. ProductService ⚠️ CRITICAL
**Status**: ❌ BROKEN - Path Mismatch

**Problem**: 
- Backend controller uses `/products` as base path
- Gateway expects `/api/v2/product/**`
- Frontend calls `/api/v2/product/**`

**Backend Implementation**:
```java
@RestController
@RequestMapping("/products")  // ❌ WRONG
public class ProductController {
```

**Frontend Calls**:
- `/api/v2/product/create-product`
- `/api/v2/product/get-all-products`
- `/api/v2/product/get-all-products-shop/{id}`
- `/api/v2/product/delete-shop-product/{id}`

**Gateway Route**:
```properties
spring.cloud.gateway.server.webflux.routes[1].predicates[0]=Path=/api/v2/product/**
```

**Fix Required**: Change ProductService controller base path to `/api/v2/product`

---

### 2. CartService ⚠️ CRITICAL
**Status**: ❌ BROKEN - Path Mismatch

**Problem**:
- Backend controller uses `/carts` as base path
- Gateway expects `/api/v2/cart/**`
- Frontend calls `/carts/**` (missing /api/v2 prefix)

**Backend Implementation**:
```java
@RestController
@RequestMapping("/carts")  // Needs /api/v2/cart
public class CartController {
```

**Frontend Calls**:
- `/carts/{userId}` - POST (create cart)
- `/carts/{userId}` - GET (get cart)
- `/carts/{cartId}/items` - POST (add item)
- `/carts/{cartId}/items/{productId}` - DELETE (remove item)
- `/carts/{cartId}/clear` - DELETE (clear cart)

**Gateway Route**:
```properties
spring.cloud.gateway.server.webflux.routes[2].predicates[0]=Path=/api/v2/cart/**
```

**Fixes Required**:
1. Change CartService controller base path to `/api/v2/cart`
2. OR Update frontend to call `/api/v2/cart/**`
3. OR Add gateway compatibility rewrites

---

### 3. Event Service ⚠️ NOT MIGRATED
**Status**: ❌ BROKEN - Service Not Migrated

**Problem**:
- Events are still in old Node.js backend (`backend/controller/event.js`)
- Not migrated to microservices yet
- Frontend calls event endpoints but no microservice exists

**Frontend Calls**:
- `/api/v2/event/create-event`
- `/api/v2/event/get-all-events/{id}`
- `/api/v2/event/get-all-events`
- `/api/v2/event/delete-shop-event/{id}`

**Gateway Route**: ❌ NO ROUTE EXISTS

**Fixes Required**:
1. Create EventService microservice
2. Migrate event logic from Node.js backend
3. Add gateway routing for `/api/v2/event/**`
4. OR Route `/api/v2/event/**` to old Node.js backend temporarily

---

### 4. Shop Admin Endpoints ⚠️ NOT FOUND
**Status**: ❌ UNKNOWN - Endpoint Not Found

**Problem**:
- Frontend calls `/api/v2/shop/admin-all-sellers`
- No corresponding endpoint found in ShopService

**Frontend Call**:
- `/api/v2/shop/admin-all-sellers` - GET (get all sellers for admin)

**Fix Required**: Add admin endpoint to ShopService

---

## 📊 Service-by-Service Breakdown

| Service | Backend Path | Frontend Calls | Gateway Path | Status |
|---------|-------------|----------------|--------------|--------|
| UserService | `/api/auth/**` | `/api/v2/user/**` | ✅ Mapped | ✅ Working |
| ShopService | `/api/v2/shop/**` | `/api/v2/shop/**` | ✅ Mapped | ✅ Working |
| ProductService | ❌ `/products` | `/api/v2/product/**` | `/api/v2/product/**` | ❌ BROKEN |
| CartService | ❌ `/carts` | `/carts/**` | `/api/v2/cart/**` | ❌ BROKEN |
| OrderService | `/api/v2/order/**` | `/api/v2/order/**` | ✅ Mapped | ✅ Working |
| PaymentService | `/api/v2/payment/**` | `/api/v2/payment/**` | ✅ Mapped | ✅ Working |
| ChatService | `/api/v2/conversation/**` `/api/v2/message/**` | Same | ✅ Mapped | ✅ Working |
| EventService | ❌ Not Migrated | `/api/v2/event/**` | ❌ No Route | ❌ BROKEN |
| NotificationService | `/api/v2/notification/**` | N/A | ✅ Mapped | ⚠️ Not Used |

---

## 🔧 Required Fixes

### Priority 1: CRITICAL (Blocks Core Functionality)

#### Fix 1: ProductService Path Mismatch
**File**: `ThriftKaro 2.0/ProductService/src/main/java/com/productservice/controller/ProductController.java`

**Change**:
```java
@RestController
-@RequestMapping("/products")
+@RequestMapping("/api/v2/product")
public class ProductController {
```

#### Fix 2: CartService Path Mismatch
**Option A - Update Backend** (Recommended):
**File**: `ThriftKaro 2.0/cart/src/main/java/com/cart/controller/CartController.java`

```java
@RestController
-@RequestMapping("/carts")
+@RequestMapping("/api/v2/cart")
public class CartController {
```

**Option B - Update Frontend**:
Update `frontend/src/api/cartService.js` to use `/api/v2/cart/` prefix instead of `/carts/`

#### Fix 3: Event Service Migration
**Option A - Create Microservice** (Recommended):
1. Create new EventService microservice
2. Migrate logic from `backend/controller/event.js`
3. Add gateway routing

**Option B - Temporary Routing** (Quick Fix):
Add old Node.js backend route to gateway for `/api/v2/event/**`

### Priority 2: HIGH (Missing Features)

#### Fix 4: Shop Admin Endpoints
**File**: `ThriftKaro 2.0/ShopService/src/main/java/com/shopservice/controller/ShopController.java`

Add endpoint:
```java
@GetMapping("/admin-all-sellers")
public ResponseEntity<?> getAllSellers(@RequestHeader("Authorization") String token) {
    // Implement admin endpoint
}
```

---

## 🛠️ Gateway Configuration Issues

### Missing Routes

1. **Events**: No route for `/api/v2/event/**`
2. **Product Compatibility**: May need rewrites for legacy endpoints

### Allowlist Configuration
Current JWT allowlist might be too permissive:
```properties
jwt.allowlist=/api/auth/**,/api/v2/user/**,/api/v2/product/**,/api/v2/cart/**,/api/v2/shop/register,/api/v2/shop/login,/api/v2/payment/stripeapikey,/actuator/**
```

**Recommendation**: Review which endpoints should be public vs authenticated

---

## 📝 Frontend API Patterns

### Current Pattern
```javascript
// Frontend uses axiosInstance with baseURL = http://localhost:8080/api/v2
axiosInstance.post('/product/create-product', data)
// Translates to: http://localhost:8080/api/v2/product/create-product
```

### Issues Found
1. **CartService**: Doesn't use `/api/v2` prefix in some calls
2. **Event Service**: Calls non-existent microservice
3. **Product Service**: Path doesn't match backend controller

---

## 🎯 Recommended Action Plan

### Phase 1: Immediate Fixes (Required for MVP)
1. ✅ Fix ProductService controller path → `/api/v2/product`
2. ✅ Fix CartService controller path → `/api/v2/cart`  
3. ✅ Add gateway route for old backend events (temporary)
4. ✅ Add Shop admin-all-sellers endpoint

### Phase 2: Service Migration (Post-MVP)
1. Create EventService microservice
2. Migrate event logic from Node.js backend
3. Update gateway routing
4. Remove old backend dependencies

### Phase 3: Optimization
1. Review JWT allowlist
2. Add rate limiting per service
3. Add retry policies for critical services
4. Implement proper circuit breakers

---

## 🧪 Testing Checklist

After fixes, test the following flows:

### User Flow
- [x] User signup
- [x] User login
- [ ] User profile update

### Shop/Seller Flow
- [x] Shop registration
- [x] Shop login
- [ ] Create product ❌ (Blocked by ProductService path)
- [ ] View shop products ❌ (Blocked by ProductService path)
- [ ] Create event ❌ (Service not migrated)

### Shopping Flow
- [ ] View all products ❌ (Blocked by ProductService path)
- [ ] Add to cart ❌ (Blocked by CartService path)
- [ ] Checkout
- [ ] View orders

### Admin Flow
- [ ] View all orders ✅
- [ ] View all sellers ❌ (Endpoint missing)
- [ ] View all products ❌ (Blocked by ProductService path)

---

## 📦 Microservices Status Summary

| Service | Port | Status | Database | Issues |
|---------|------|--------|----------|---------|
| EurekaServer | 8761 | ✅ Running | N/A | None |
| ConfigServer | 8888 | ✅ Running | N/A | None |
| Gateway | 8080 | ✅ Running | N/A | None |
| UserService | Auto | ✅ Fixed | MongoDB | None |
| ShopService | 8089 | ✅ Fixed | MongoDB | Missing admin endpoint |
| ProductService | 8081 | ❌ Path Issue | MongoDB | Controller path wrong |
| CartService | 8083 | ❌ Path Issue | MySQL | Controller path wrong |
| OrderService | 8084 | ✅ Working | MongoDB | None |
| PaymentService | 8085 | ✅ Working | MongoDB | None |
| ChatService | 8086 | ✅ Working | MongoDB | None |
| NotificationService | Auto | ⚠️ Unused | MongoDB | Not integrated |
| EventService | N/A | ❌ Not Migrated | N/A | Needs creation |

---

## 🔍 Additional Notes

### Old Backend (Node.js Monolith)
Located in `backend/` folder, contains:
- Event controller (not migrated)
- Product controller (might have legacy endpoints)
- Shop controller (migrated but might have legacy dependencies)
- User controller (migrated)
- Order, Payment, Message, Conversation controllers (migrated)

**Recommendation**: Keep old backend running temporarily for event functionality

### Environment Variables
Ensure these are set:
- `JWT_SECRET`: Same across all services
- `STRIPE_PUBLIC_KEY`: For payment service
- `MONGODB_URI`: For each service's database

---

**End of Audit Report**



