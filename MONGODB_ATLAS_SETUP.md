# MongoDB Atlas Setup Instructions for ThriftKaro

## ✅ Completed Steps:
1. Updated all application.properties files with Atlas connection strings
2. Configured environment variable support for secure credential management

## 🔧 Next Steps for You:

### 1. Replace Placeholder Values
In all the updated application.properties files, replace these placeholders with your actual Atlas credentials:

- `<password>` → Your actual database user password
- `thriftkaro-cluster.xxxxx.mongodb.net` → Your actual cluster connection string

### 2. Environment Variables (Recommended)
Create a `.env` file in your project root with your actual credentials:

```bash
# MongoDB Atlas Connection Strings
USER_MONGODB_URI=mongodb+srv://thriftkaro-admin:YOUR_PASSWORD@YOUR_CLUSTER.mongodb.net/thriftkaro_user_db?retryWrites=true&w=majority
PRODUCT_MONGODB_URI=mongodb+srv://thriftkaro-admin:YOUR_PASSWORD@YOUR_CLUSTER.mongodb.net/thriftkaro_product_db?retryWrites=true&w=majority
SHOP_MONGODB_URI=mongodb+srv://thriftkaro-admin:YOUR_PASSWORD@YOUR_CLUSTER.mongodb.net/thriftkaro_shop_db?retryWrites=true&w=majority
ORDER_MONGODB_URI=mongodb+srv://thriftkaro-admin:YOUR_PASSWORD@YOUR_CLUSTER.mongodb.net/thriftkaro_order_db?retryWrites=true&w=majority
CART_MONGODB_URI=mongodb+srv://thriftkaro-admin:YOUR_PASSWORD@YOUR_CLUSTER.mongodb.net/thriftkaro_cart_db?retryWrites=true&w=majority
PAYMENT_MONGODB_URI=mongodb+srv://thriftkaro-admin:YOUR_PASSWORD@YOUR_CLUSTER.mongodb.net/thriftkaro_payment_db?retryWrites=true&w=majority
CHAT_MONGODB_URI=mongodb+srv://thriftkaro-admin:YOUR_PASSWORD@YOUR_CLUSTER.mongodb.net/thriftkaro_chat_db?retryWrites=true&w=majority
```

### 3. Database Collections Setup
Your microservices will automatically create the required collections when they start, but you can also create them manually in Atlas:

**Databases to be created:**
- thriftkaro_user_db
- thriftkaro_product_db  
- thriftkaro_shop_db
- thriftkaro_order_db
- thriftkaro_cart_db
- thriftkaro_payment_db
- thriftkaro_chat_db

### 4. Testing the Connection
After updating the credentials, start your microservices. You should see:
- ✅ Successful MongoDB connections in logs
- ❌ No more "Connection refused" errors

### 5. Security Best Practices
- Never commit passwords to version control
- Use environment variables for sensitive data
- Consider using MongoDB Atlas IP whitelist for production
- Regularly rotate database passwords

## 🚀 Ready to Test!
Once you've completed the Atlas setup and updated the credentials, your microservices should connect successfully to MongoDB Atlas cloud database.
