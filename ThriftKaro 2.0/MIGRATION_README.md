# ThriftKaro 2.0 - Microservices Migration

This document describes the complete migration from Node.js monolithic backend to Spring Boot microservices architecture.

## 🏗️ Architecture Overview

### Services Created/Updated:

1. **Eureka Server** (Port: 8761) - Service Registry
2. **Config Server** (Port: 8888) - Centralized Configuration
3. **API Gateway** (Port: 8080) - Request Routing & Load Balancing
4. **User Service** (Port: 8081) - Authentication & User Management
5. **Product Service** (Port: 8082) - Product Catalog Management
6. **Cart Service** (Port: 8083) - Shopping Cart Operations
7. **Order Service** (Port: 8084) - Order Management, Withdrawals, Coupons
8. **Chat Service** (Port: 8085) - Real-time Messaging
9. **Notification Service** (Port: 8086) - Email Notifications

## 🔄 API Endpoint Mapping

### Original Node.js Routes → New Microservice Routes

| Original Route | New Route | Service |
|----------------|-----------|---------|
| `/api/v2/user/**` | `/api/v2/user/**` | User Service |
| `/api/v2/product/**` | `/api/v2/product/**` | Product Service |
| `/api/v2/shop/**` | `/api/v2/shop/**` | User Service (Shop Management) |
| `/api/v2/cart/**` | `/api/v2/cart/**` | Cart Service |
| `/api/v2/order/**` | `/api/v2/order/**` | Order Service |
| `/api/v2/withdraw/**` | `/api/v2/withdraw/**` | Order Service |
| `/api/v2/coupon/**` | `/api/v2/coupon/**` | Order Service |
| `/api/v2/conversation/**` | `/api/v2/conversation/**` | Chat Service |
| `/api/v2/message/**` | `/api/v2/message/**` | Chat Service |
| `/api/v2/notification/**` | `/api/v2/notification/**` | Notification Service |

## 🗄️ Database Migration

### MongoDB Collections:
- **thriftkaro_user_db**: User Service
- **thriftkaro_product_db**: Product Service  
- **thriftkaro_cart_db**: Cart Service
- **thriftkaro_order_db**: Order Service
- **thriftkaro_chat_db**: Chat Service

### Environment Variables Required:
```bash
# MongoDB
MONGODB_URI=mongodb://localhost:27017

# JWT
JWT_SECRET=mySecretKeyForJWTTokenGenerationThatIsLongEnoughForSecurityRequirements
JWT_EXPIRATION=86400000

# Email (SMTP)
SMPT_HOST=smtp.gmail.com
SMPT_PORT=587
SMPT_MAIL=your-email@gmail.com
SMPT_PASSWORD=your-app-password

# Cloudinary (for image uploads)
CLOUDINARY_NAME=your-cloud-name
CLOUDINARY_API_KEY=your-api-key
CLOUDINARY_API_SECRET=your-api-secret

# Stripe (for payments)
STRIPE_SECRET_KEY=your-stripe-secret-key
STRIPE_PUBLISHABLE_KEY=your-stripe-publishable-key
```

## 🚀 How to Run

### Prerequisites:
- Java 21+
- Maven 3.6+
- MongoDB running on localhost:27017
- Redis (optional, for rate limiting)

### Quick Start:
1. Navigate to `ThriftKaro 2.0` directory
2. Run `start-services.bat` (Windows) or start services manually
3. Services will start in the correct order with proper delays

### Manual Service Start Order:
1. Eureka Server (15s delay)
2. Config Server (10s delay)
3. API Gateway (10s delay)
4. User Service (10s delay)
5. Product Service (10s delay)
6. Cart Service (10s delay)
7. Order Service (10s delay)
8. Chat Service (10s delay)
9. Notification Service

## 🔧 Service Details

### Order Service Features:
- Order creation and management
- Withdrawal request handling
- Coupon code management
- Order status updates
- Refund processing

### Chat Service Features:
- Conversation management
- Real-time messaging
- Image sharing support
- Message history

### Notification Service Features:
- Email notifications
- Withdrawal request emails
- Payment confirmation emails
- Error handling

## 🔒 Security

- JWT authentication maintained across all services
- Same security model as original Node.js backend
- API Gateway handles authentication routing
- Service-to-service communication secured

## 📊 Monitoring

- Eureka Dashboard: http://localhost:8761
- All services register with Eureka automatically
- Health checks available for each service
- Circuit breakers implemented in API Gateway

## 🐛 Troubleshooting

### Common Issues:
1. **Port conflicts**: Ensure no other services are using the assigned ports
2. **MongoDB connection**: Verify MongoDB is running and accessible
3. **Service discovery**: Check Eureka server is running first
4. **JWT issues**: Verify JWT secret is consistent across services

### Service Health Checks:
- Eureka: http://localhost:8761
 - API Gateway: http://localhost:8089/actuator/health
- Individual services: http://localhost:PORT/actuator/health

## 🔄 Migration Benefits

1. **Scalability**: Each service can be scaled independently
2. **Maintainability**: Clear separation of concerns
3. **Technology Flexibility**: Services can use different technologies
4. **Fault Isolation**: Failure in one service doesn't affect others
5. **Team Independence**: Different teams can work on different services

## 📝 Notes

- All original API endpoints are preserved for frontend compatibility
- Database schemas remain the same
- JWT tokens work across all services
- Email functionality maintained
- Real-time chat capabilities preserved
- Payment processing logic maintained

## 🎯 Next Steps

1. Test all API endpoints
2. Verify database connections
3. Test email functionality
4. Validate JWT authentication
5. Test service-to-service communication
6. Deploy to production environment
