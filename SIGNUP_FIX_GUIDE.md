# Signup Functionality Fix Guide

## Issues Identified

### 1. Backend Not Handling Avatar Field
**Problem:** The frontend sends `avatar` field, but the backend `RegisterRequest` DTO didn't have this field, so it was being ignored.

**Fix:** Added `avatar` field to `RegisterRequest.java`

### 2. Response Format Mismatch
**Problem:** Frontend expects `{ success, message, token, user }`, but backend was returning just a string.

**Fix:** Updated `AuthController.java` to return proper JSON response with all required fields.

### 3. Avatar Not Being Saved
**Problem:** Even if avatar was in the request, the controller wasn't setting it on the User entity.

**Fix:** Added avatar handling in the controller to save it as a Map object in MongoDB.

## Files Modified

1. `ThriftKaro 2.0/UserService/src/main/java/com/userservice/dtos/RegisterRequest.java`
   - Added `avatar` field

2. `ThriftKaro 2.0/UserService/src/main/java/com/userservice/controller/AuthController.java`
   - Enhanced registration endpoint to:
     - Handle avatar field
     - Return proper JSON response with token and user data
     - Better error handling

## Steps to Apply the Fix

### 1. Rebuild User Service

```bash
cd "ThriftKaro 2.0/UserService"
mvn clean compile
```

Or use the provided script:
```bash
cd "ThriftKaro 2.0"
rebuild-user-service.bat
```

### 2. Restart Your Services

**Option A: Start all services fresh**
```bash
cd "ThriftKaro 2.0"
start-services.bat
```

**Option B: Restart only User Service (if others are running)**
```bash
cd "ThriftKaro 2.0/UserService"
mvn spring-boot:run
```

### 3. Start Frontend (if not running)
```bash
cd frontend
npm start
```

## Testing the Signup

1. Navigate to: `http://localhost:3000/sign-up`
2. Fill in the form:
   - Full Name
   - Email
   - Password
   - Upload Avatar (optional)
3. Click Submit
4. Check browser console (F12) for any errors
5. Check Network tab to see the request/response

## Expected API Flow

### Request
```
POST http://localhost:8080/api/v2/user/create-user
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "avatar": "data:image/png;base64,..." (optional)
}
```

### Gateway Routing
The gateway will rewrite:
- `/api/v2/user/create-user` → `lb://UserService/api/auth/register`

### Response (Success)
```json
{
  "success": true,
  "message": "User registered successfully!",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "_id": "507f1f77bcf86cd799439011",
    "name": "John Doe",
    "email": "john@example.com",
    "role": "USER",
    "avatar": {
      "url": "data:image/png;base64,..."
    },
    "addresses": [],
    "createdAt": "2025-10-25T..."
  }
}
```

### Response (Error - User Exists)
```json
{
  "message": "User already exists with this email!"
}
```

## Troubleshooting

### Issue: "Network Error" or Request Times Out

**Check:**
1. Is Eureka Server running? → `http://localhost:8761`
2. Is Gateway running? → `http://localhost:8080`
3. Is User Service registered with Eureka?
   - Visit `http://localhost:8761`
   - Look for "USERSERVICE" in the instances list

### Issue: "Email already registered" immediately

**Solution:** The user already exists in database. Try with a different email or delete the user from MongoDB:

```bash
# Connect to MongoDB Atlas and run:
db.users.deleteOne({ email: "your-test-email@example.com" })
```

### Issue: Services won't start

**Check:**
1. Java version: `java -version` (should be 17+)
2. Maven installed: `mvn -version`
3. MongoDB Atlas connection string is correct in `application.properties`
4. Port conflicts (8080, 8761, 8082, etc.)

### Issue: "Cannot connect to database"

**Check:**
1. MongoDB Atlas connection string in:
   - `ThriftKaro 2.0/UserService/src/main/resources/application.properties`
   - Look for: `spring.data.mongodb.uri`
2. Network connection to MongoDB Atlas
3. MongoDB Atlas IP whitelist settings (0.0.0.0/0 for testing)

## Service URLs

| Service | URL | Purpose |
|---------|-----|---------|
| Frontend | http://localhost:3000 | React app |
| Gateway | http://localhost:8080 | API Gateway (main entry point) |
| Eureka | http://localhost:8761 | Service discovery |
| User Service | http://localhost:8082 | Direct user service (usually accessed via gateway) |

## Browser Console Debugging

Open browser console (F12) and look for:

1. **Request sent:**
   ```
   POST http://localhost:8080/api/v2/user/create-user
   ```

2. **Response received:**
   - Status: 200 OK (success) or 4xx/5xx (error)
   - Body: Should contain `{ success, message, token, user }`

3. **Toast notification:**
   - Success: "User registered successfully!"
   - Error: Will show the error message from backend

## Database Verification

After successful signup, verify in MongoDB Atlas:

1. Login to MongoDB Atlas
2. Browse Collections → `thriftkaro_user_db` → `users`
3. Find your newly created user
4. Check fields:
   - `name`, `email`, `passwordHash` (encrypted)
   - `avatar` (if provided)
   - `role` = "USER"
   - `createdAt` timestamp

## Next Steps After Successful Signup

1. User should be automatically logged in (token stored in localStorage)
2. Redirect to home page or dashboard
3. Test login with the same credentials
4. Test other user features

## Need More Help?

Check the console logs:
- **Frontend**: Browser console (F12) → Console tab
- **Backend**: Terminal window running the services
- **Gateway**: Gateway service terminal
- **User Service**: User Service terminal


