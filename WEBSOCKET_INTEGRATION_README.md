# WebSocket Chat Integration - ThriftKaro

This document describes the integration of real-time WebSocket chat functionality into the existing ChatService Spring Boot microservice, replacing the separate Node.js socket server.

## Architecture Overview

The WebSocket integration uses:
- **Spring Boot WebSocket** with STOMP over SockJS
- **Frontend**: SockJS + @stomp/stompjs library
- **API Gateway**: Routes WebSocket connections to ChatService
- **Database**: Messages are persisted to MongoDB

## Backend Implementation

### 1. Dependencies Added
Added to `chat-service/pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

### 2. WebSocket Configuration
**File**: `chat-service/src/main/java/com/chatservice/config/WebSocketConfig.java`

- **WebSocket endpoint**: `/ws`
- **Application destination prefix**: `/app`
- **Message broker destinations**: `/topic` and `/queue`
- **SockJS fallback**: Enabled for browser compatibility

### 3. WebSocket Controller
**File**: `chat-service/src/main/java/com/chatservice/controller/WebSocketChatController.java`

**Mappings**:
- `@MessageMapping("/chat.send")` → `@SendTo("/topic/public")`
- `@MessageMapping("/chat.addUser")` → `@SendTo("/topic/public")`
- `@MessageMapping("/chat.leave")` → `@SendTo("/topic/public")`

**Features**:
- Real-time message broadcasting
- User join/leave notifications
- Automatic message persistence to database
- Error handling with graceful fallback

### 4. API Gateway Configuration
**File**: `gateway/src/main/resources/application.properties`

Added WebSocket routing:
```properties
# WebSocket Chat Service
spring.cloud.gateway.server.webflux.routes[9].id=websocket-chat
spring.cloud.gateway.server.webflux.routes[9].uri=lb://ChatService
spring.cloud.gateway.server.webflux.routes[9].predicates[0]=Path=/ws/**
```

## Frontend Implementation

### 1. Dependencies Installed
```bash
npm install @stomp/stompjs sockjs-client --legacy-peer-deps
```

### 2. WebSocket Service
**File**: `frontend/src/services/websocketService.js`

**Features**:
- Singleton service for WebSocket management
- Automatic connection/disconnection handling
- Message subscription and publishing
- User join/leave functionality

### 3. Updated Components
- **UserInbox.jsx**: User chat interface
- **DashboardMessages.jsx**: Seller chat interface

**Changes**:
- Replaced Socket.IO with STOMP over SockJS
- Maintained existing UI/UX
- Added real-time message updates
- Preserved message persistence

## Testing Instructions

### 1. Start Services
```bash
# Start Eureka Server
cd "ThriftKaro 2.0/EurekaServer"
mvn spring-boot:run

# Start API Gateway
cd "ThriftKaro 2.0/gateway"
mvn spring-boot:run

# Start ChatService
cd "ThriftKaro 2.0/chat-service"
mvn spring-boot:run

# Start Frontend
cd frontend
npm start
```

### 2. Test WebSocket Connection
Open `websocket-test.html` in a browser to test the WebSocket connection:

1. Enter User ID (e.g., "user1")
2. Enter Conversation ID (e.g., "conv1")
3. Click "Connect"
4. Send messages to test real-time communication

### 3. Test End-to-End Flow
1. **Start all services** (Eureka, Gateway, ChatService, Frontend)
2. **Create users** through the frontend
3. **Start a conversation** between users
4. **Send messages** and verify:
   - Real-time delivery
   - Message persistence in database
   - User join/leave notifications

## WebSocket Endpoints

### Connection
- **URL**: `ws://localhost:8080/ws`
- **Protocol**: STOMP over SockJS
- **Fallback**: HTTP polling

### Message Destinations
- **Send messages**: `/app/chat.send`
- **Join chat**: `/app/chat.addUser`
- **Leave chat**: `/app/chat.leave`
- **Receive messages**: `/topic/public`

## Message Format

```json
{
  "id": "message-id",
  "conversationId": "conversation-id",
  "text": "message text",
  "sender": "user-id",
  "images": {
    "public_id": "image-id",
    "url": "image-url"
  },
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00",
  "type": "CHAT|JOIN|LEAVE"
}
```

## Migration Notes

### Removed Components
- **Node.js Socket Server**: `socket/index.js` (can be removed)
- **Socket.IO dependencies**: Replaced with STOMP/SockJS

### Preserved Functionality
- Message persistence to MongoDB
- User authentication and authorization
- Image sharing capabilities
- Conversation management
- Real-time notifications

## Troubleshooting

### Common Issues

1. **WebSocket Connection Failed**
   - Check if all services are running
   - Verify API Gateway routing configuration
   - Check browser console for CORS errors

2. **Messages Not Persisting**
   - Verify MongoDB connection
   - Check ChatService logs for database errors
   - Ensure MessageService is properly configured

3. **Real-time Updates Not Working**
   - Check WebSocket connection status
   - Verify STOMP subscription is active
   - Check browser network tab for WebSocket frames

### Debug Commands
```bash
# Check service registration
curl http://localhost:8761/eureka/apps

# Test WebSocket endpoint
curl -i -N -H "Connection: Upgrade" -H "Upgrade: websocket" -H "Sec-WebSocket-Version: 13" -H "Sec-WebSocket-Key: x3JJHMbDL1EzLkh9GBhXDw==" http://localhost:8080/ws
```

## Performance Considerations

- **Connection Pooling**: WebSocket connections are managed efficiently
- **Message Broadcasting**: Uses Spring's in-memory broker for low latency
- **Database Optimization**: Messages are batched and indexed properly
- **Fallback Support**: SockJS provides HTTP polling fallback for restricted networks

## Security

- **CORS Configuration**: Properly configured for frontend origin
- **Authentication**: Integrates with existing JWT authentication
- **Message Validation**: Input validation on all WebSocket messages
- **Rate Limiting**: Can be extended with Spring Security WebSocket support
