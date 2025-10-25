# Signup Functionality - Testing Checklist

## Pre-Testing Setup

### Step 1: Rebuild User Service (IMPORTANT!)
```bash
cd "ThriftKaro 2.0"
rebuild-user-service.bat
```

Wait for: `BUILD SUCCESS`

### Step 2: Start Core Services

**Option A: Quick Start (Eureka + Gateway + User Service only)**
```bash
cd "ThriftKaro 2.0"
quick-start-core-services.bat
```

**Option B: Full System (All microservices)**
```bash
cd "ThriftKaro 2.0"
start-services.bat
```

### Step 3: Verify Services Started
1. Wait 30-60 seconds for services to initialize
2. Open Eureka Dashboard: http://localhost:8761
3. Check that "USERSERVICE" appears in the instances list
4. Should show status: UP (1) - (1) instance

### Step 4: Start Frontend
Open a new terminal:
```bash
cd frontend
npm start
```

Frontend will open at: http://localhost:3000

---

## Testing Signup Flow

### Test Case 1: Successful Signup (New User)

1. **Navigate to Signup Page**
   - URL: http://localhost:3000/sign-up
   - Or click "Sign Up" link from login page

2. **Fill the Form**
   - Full Name: `Test User`
   - Email: `testuser@example.com` (use a unique email)
   - Password: `password123`
   - Avatar: Upload a profile picture (optional)

3. **Open Browser Console** (F12)
   - Go to Console tab
   - Clear any previous logs

4. **Click Submit Button**

5. **Expected Results:**
   
   ✅ Console logs:
   ```
   Signup form submitted with data: {...}
   Signup successful: {success: true, message: "...", token: "...", user: {...}}
   ```
   
   ✅ Success toast appears: "User registered successfully!"
   
   ✅ Form fields clear
   
   ✅ Network tab (F12 → Network):
   - Request: POST http://localhost:8080/api/v2/user/create-user
   - Status: 200 OK
   - Response contains: `{ success, message, token, user }`
   
   ✅ localStorage updated:
   - Open Console → Application → Local Storage → http://localhost:3000
   - Should see `token` and `user` entries

### Test Case 2: Duplicate Email

1. Try to signup again with the same email
2. **Expected Results:**
   
   ❌ Error toast: "User already exists with this email!"
   
   ❌ Console shows error message
   
   ❌ Network tab shows: Status 400 Bad Request

### Test Case 3: Signup with Avatar

1. Use a different email
2. Upload an image file (.jpg, .jpeg, or .png)
3. Submit form
4. **Expected Results:**
   
   ✅ Same as Test Case 1
   
   ✅ User object in response contains avatar:
   ```json
   "avatar": {
     "url": "data:image/png;base64,..."
   }
   ```

---

## Troubleshooting

### ❌ Issue: "Network Error" in console

**Check List:**
- [ ] Eureka Server running? → http://localhost:8761
- [ ] Gateway running? → http://localhost:8080
- [ ] User Service registered in Eureka?
- [ ] No CORS errors in console?

**Solution:**
1. Stop all services
2. Restart using `quick-start-core-services.bat`
3. Wait for all services to appear in Eureka

### ❌ Issue: Request times out (10 seconds)

**Possible Causes:**
- Services not fully started yet → Wait longer (check Eureka)
- MongoDB connection issue → Check connection string
- User Service crashed → Check User Service terminal for errors

**Solution:**
```bash
# Check User Service logs in its terminal window
# Look for "Started UserServiceApplication"
```

### ❌ Issue: "Cannot POST /api/v2/user/create-user"

**Cause:** Gateway routing not configured or User Service not registered

**Solution:**
1. Check Eureka: http://localhost:8761
2. Verify USERSERVICE is listed
3. Check Gateway terminal for routing logs
4. Restart Gateway if needed

### ❌ Issue: 500 Internal Server Error

**Possible Causes:**
- Database connection failed
- Backend code error
- Missing dependencies

**Solution:**
1. Check User Service terminal for stack trace
2. Verify MongoDB Atlas connection:
   - Check `application.properties` has correct URI
   - Test connection: https://cloud.mongodb.com/
3. Check if User Service compiled successfully:
   ```bash
   cd "ThriftKaro 2.0/UserService"
   mvn clean compile
   ```

### ❌ Issue: CORS Error

**Error Message:**
```
Access to XMLHttpRequest at 'http://localhost:8080/api/v2/user/create-user' 
from origin 'http://localhost:3000' has been blocked by CORS policy
```

**Solution:**
Gateway should have CORS configured. Check `gateway/src/main/resources/application.properties`:
```properties
spring.cloud.gateway.server.webflux.globalcors.cors-configurations[/**].allowed-origins=http://localhost:3000
```

If missing, add it and restart Gateway.

### ❌ Issue: Form submits but nothing happens

**Debug Steps:**
1. Open browser console (F12)
2. Check for any error messages
3. Go to Network tab
4. Click Submit again
5. Look for the POST request to `/user/create-user`
6. Click on the request to see:
   - Request headers
   - Request payload
   - Response status
   - Response body

---

## Post-Signup Verification

### Verify in MongoDB Atlas

1. Login to MongoDB Atlas: https://cloud.mongodb.com/
2. Navigate to: Clusters → ThriftKaro-Cluster → Browse Collections
3. Database: `thriftkaro_user_db`
4. Collection: `users`
5. Find your user by email

**Expected Document:**
```json
{
  "_id": ObjectId("..."),
  "name": "Test User",
  "email": "testuser@example.com",
  "passwordHash": "$2a$10$...", // encrypted
  "role": "USER",
  "avatar": {
    "url": "data:image/png;base64,..." // if uploaded
  },
  "addresses": [],
  "createdAt": ISODate("2025-10-25T...")
}
```

### Test Auto-Login After Signup

1. After successful signup, check if user is logged in:
   - `localStorage` should have `token` and `user`
   - User should be redirected (if implemented)
   - Header should show user profile

### Test Login with New Account

1. Clear localStorage: Console → `localStorage.clear()`
2. Navigate to login page: http://localhost:3000/login
3. Enter the email and password used during signup
4. Click Login
5. Should successfully log in

---

## Success Criteria

✅ All three test cases pass
✅ User data saved in MongoDB
✅ JWT token generated and stored
✅ No console errors
✅ Can login with created account

---

## Common Port Conflicts

If services fail to start, check for port conflicts:

| Service | Default Port | How to Change |
|---------|-------------|---------------|
| Eureka | 8761 | `EurekaServer/src/main/resources/application.properties` |
| Gateway | 8080 | `gateway/src/main/resources/application.properties` |
| User Service | 8082 | `UserService/src/main/resources/application.properties` |
| Frontend | 3000 | React will prompt to use 3001 if 3000 is busy |

To check what's using a port on Windows:
```bash
netstat -ano | findstr :8080
```

---

## Quick Commands Reference

```bash
# Rebuild User Service
cd "ThriftKaro 2.0"
rebuild-user-service.bat

# Start core services
cd "ThriftKaro 2.0"
quick-start-core-services.bat

# Start frontend
cd frontend
npm start

# Check service status
# Open in browser: http://localhost:8761

# Clear test data from MongoDB (if needed)
# Connect to MongoDB Atlas and run:
db.users.deleteOne({ email: "testuser@example.com" })
```

---

## Need Help?

Check detailed guide: `SIGNUP_FIX_GUIDE.md`

Check service logs in their terminal windows for detailed error messages.


