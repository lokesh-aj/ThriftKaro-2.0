// MongoDB Database Setup Script
// Run this script using: mongo setup-mongodb-databases.js

// Create databases and collections for ThriftKaro microservices
print("Setting up ThriftKaro databases...");

// User Service Database
db = db.getSiblingDB('thriftkaro_user_db');
db.createCollection('users');
db.createCollection('userprofiles');
print("Created thriftkaro_user_db with collections: users, userprofiles");

// Product Service Database  
db = db.getSiblingDB('thriftkaro_product_db');
db.createCollection('products');
db.createCollection('categories');
db.createCollection('reviews');
print("Created thriftkaro_product_db with collections: products, categories, reviews");

// Shop Service Database
db = db.getSiblingDB('thriftkaro_shop_db');
db.createCollection('shops');
db.createCollection('shopowners');
db.createCollection('shopsettings');
print("Created thriftkaro_shop_db with collections: shops, shopowners, shopsettings");

// Order Service Database
db = db.getSiblingDB('thriftkaro_order_db');
db.createCollection('orders');
db.createCollection('orderitems');
db.createCollection('orderstatus');
print("Created thriftkaro_order_db with collections: orders, orderitems, orderstatus");

// Cart Service Database
db = db.getSiblingDB('thriftkaro_cart_db');
db.createCollection('carts');
db.createCollection('cartitems');
print("Created thriftkaro_cart_db with collections: carts, cartitems");

// Payment Service Database
db = db.getSiblingDB('thriftkaro_payment_db');
db.createCollection('payments');
db.createCollection('transactions');
print("Created thriftkaro_payment_db with collections: payments, transactions");

// Chat Service Database
db = db.getSiblingDB('thriftkaro_chat_db');
db.createCollection('conversations');
db.createCollection('messages');
print("Created thriftkaro_chat_db with collections: conversations, messages");

print("All ThriftKaro databases created successfully!");
