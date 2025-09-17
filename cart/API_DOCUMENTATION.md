# Cart Service API Documentation

## Overview
The Cart Service is a Spring Boot microservice that manages shopping carts for users. It integrates with the User Service for authentication and the Product Service for product validation.

## Base URL
```
http://localhost:8083/carts
```

## Authentication
All endpoints require JWT authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## Endpoints

### 1. Create Cart
**POST** `/carts/{userId}`

Creates a new cart for the specified user.

**Parameters:**
- `userId` (path): The ID of the user

**Response:**
```json
{
  "cartId": 1,
  "userId": 123,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00",
  "cartItems": [],
  "totalAmount": 0.0
}
```

### 2. Add Item to Cart
**POST** `/carts/{cartId}/items`

Adds an item to the cart.

**Parameters:**
- `cartId` (path): The ID of the cart

**Request Body:**
```json
{
  "productId": 456,
  "quantity": 2
}
```

**Response:**
```json
{
  "cartId": 1,
  "userId": 123,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:05:00",
  "cartItems": [
    {
      "cartItemId": 1,
      "productId": 456,
      "productName": "Product Name",
      "productPrice": 29.99,
      "quantity": 2,
      "subtotal": 59.98
    }
  ],
  "totalAmount": 59.98
}
```

### 3. Remove Item from Cart
**DELETE** `/carts/{cartId}/items/{productId}`

Removes an item from the cart.

**Parameters:**
- `cartId` (path): The ID of the cart
- `productId` (path): The ID of the product to remove

**Response:**
```json
{
  "cartId": 1,
  "userId": 123,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:10:00",
  "cartItems": [],
  "totalAmount": 0.0
}
```

### 4. Get Cart by User ID
**GET** `/carts/{userId}`

Retrieves the cart for the specified user.

**Parameters:**
- `userId` (path): The ID of the user

**Response:**
```json
{
  "cartId": 1,
  "userId": 123,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:05:00",
  "cartItems": [
    {
      "cartItemId": 1,
      "productId": 456,
      "productName": "Product Name",
      "productPrice": 29.99,
      "quantity": 2,
      "subtotal": 59.98
    }
  ],
  "totalAmount": 59.98
}
```

### 5. Clear Cart
**DELETE** `/carts/{cartId}/clear`

Removes all items from the cart.

**Parameters:**
- `cartId` (path): The ID of the cart

**Response:**
- Status: 204 No Content

## Error Responses

### 400 Bad Request
```json
{
  "error": "Insufficient stock for product: Product Name"
}
```

### 403 Forbidden
```json
{
  "error": "Access denied"
}
```

### 404 Not Found
```json
{
  "error": "Cart not found"
}
```

### 500 Internal Server Error
```json
{
  "error": "Internal server error"
}
```

## Database Schema

### Cart Table
- `cart_id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- `user_id` (BIGINT, NOT NULL)
- `created_at` (DATETIME)
- `updated_at` (DATETIME)

### CartItem Table
- `cart_item_id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- `cart_id` (BIGINT, FOREIGN KEY)
- `product_id` (BIGINT, NOT NULL)
- `quantity` (INT, NOT NULL)

## Integration

### User Service
- Validates JWT tokens
- Extracts user ID from tokens
- Ensures users can only access their own carts

### Product Service
- Validates product existence
- Checks product stock availability
- Provides product details for cart items

## Configuration

### Application Properties
```properties
spring.application.name=cart
server.port=8083
spring.datasource.url=jdbc:mysql://localhost:3306/thriftkaro_cart_db
product.service.url=http://localhost:8081
jwt.secret=mySecretKey
jwt.expiration=86400000
```

## Security Features

1. **JWT Authentication**: All endpoints require valid JWT tokens
2. **User Validation**: Users can only access their own carts
3. **Cart Ownership**: Validates cart ownership before operations
4. **Product Validation**: Integrates with Product Service for validation

## Dependencies

- Spring Boot 3.5.5
- Spring Data JPA
- Spring Security
- Spring Cloud Netflix Eureka Client
- MySQL Connector
- Lombok
- JWT (jjwt)







