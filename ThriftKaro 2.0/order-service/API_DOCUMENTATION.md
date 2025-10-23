# Order Service API Documentation

## Overview
The Order Service is a microservice that handles order management for the ThriftKaro e-commerce platform.

**Base URL:** `http://localhost:8084/api/orders`
**Port:** 8084
**Database:** thriftkaro_order_db

## Authentication
All endpoints require JWT authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## Endpoints

### 1. Place a New Order
**POST** `/api/orders`

**Request Body:**
```json
{
    "productId": 1,
    "quantity": 2
}
```

**Response (201 Created):**
```json
{
    "id": 1,
    "userId": 1,
    "productId": 1,
    "quantity": 2,
    "totalPrice": 200.00,
    "status": "PENDING",
    "orderDate": "2024-01-15T10:30:00"
}
```

### 2. Get Order by ID
**GET** `/api/orders/{id}`

**Response (200 OK):**
```json
{
    "id": 1,
    "userId": 1,
    "productId": 1,
    "quantity": 2,
    "totalPrice": 200.00,
    "status": "PENDING",
    "orderDate": "2024-01-15T10:30:00"
}
```

### 3. Get Orders by User ID
**GET** `/api/orders/user/{userId}`

**Response (200 OK):**
```json
[
    {
        "id": 1,
        "userId": 1,
        "productId": 1,
        "quantity": 2,
        "totalPrice": 200.00,
        "status": "PENDING",
        "orderDate": "2024-01-15T10:30:00"
    }
]
```

### 4. Update Order Status
**PUT** `/api/orders/{id}/status?value=COMPLETED`

**Response (200 OK):**
```json
{
    "id": 1,
    "userId": 1,
    "productId": 1,
    "quantity": 2,
    "totalPrice": 200.00,
    "status": "COMPLETED",
    "orderDate": "2024-01-15T10:30:00"
}
```

### 5. Get All Orders
**GET** `/api/orders`

**Response (200 OK):**
```json
[
    {
        "id": 1,
        "userId": 1,
        "productId": 1,
        "quantity": 2,
        "totalPrice": 200.00,
        "status": "PENDING",
        "orderDate": "2024-01-15T10:30:00"
    }
]
```

## Order Status Values
- `PENDING` - Order has been placed but not yet processed
- `COMPLETED` - Order has been successfully processed
- `CANCELLED` - Order has been cancelled

## Error Responses

### 400 Bad Request
```json
{
    "error": "Invalid request data"
}
```

### 401 Unauthorized
```json
{
    "error": "Authentication required"
}
```

### 404 Not Found
```json
{
    "error": "Order not found"
}
```

## Database Schema

### Orders Table
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key, auto-increment |
| user_id | BIGINT | Foreign key to user |
| product_id | BIGINT | Foreign key to product |
| quantity | INT | Number of items ordered |
| total_price | DECIMAL(10,2) | Total price of the order |
| status | VARCHAR | Order status |
| order_date | DATETIME | When the order was placed |

## Notes
- The service uses JWT tokens for authentication
- All endpoints require valid authentication
- The service is registered with Eureka Discovery Server
- Product pricing is currently hardcoded (should be integrated with Product Service in production)

