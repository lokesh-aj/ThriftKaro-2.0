# 🔧 ThriftKaro Microservices Troubleshooting Guide

## ❌ **Java Source Version Error**

### **Error:**
```
java: Source option 5 is no longer supported. Use 8 or later.
```

### **Solution:**
1. **Check Java Version:**
   ```bash
   java -version
   javac -version
   ```
   Should show Java 21 or later.

2. **Clean and Recompile:**
   ```bash
   # For each service
   mvn clean compile
   mvn spring-boot:run
   ```

3. **Check Maven Configuration:**
   - All services now have explicit Java 21 configuration
   - Maven compiler plugin version 3.11.0
   - Source and target set to 21

---

## 🚀 **Quick Fix Commands**

### **Option 1: Use the Batch Script**
```bash
start-services.bat
```

### **Option 2: Manual Start (Recommended)**
```bash
# 1. Start Eureka Server
cd EurekaServer
mvn clean compile spring-boot:run

# 2. Start User Service (new terminal)
cd UserService
mvn clean compile spring-boot:run

# 3. Start Product Service (new terminal)
cd ProductService
mvn clean compile spring-boot:run

# 4. Start Cart Service (new terminal)
cd cart
mvn clean compile spring-boot:run
```

---

## 🔍 **Common Issues & Solutions**

### **1. Spring Boot/Cloud Version Compatibility**
- **Fixed:** All services now use Spring Boot 3.4.0 + Spring Cloud 2024.0.0
- **Check:** No more compatibility warnings

### **2. Java Version Issues**
- **Fixed:** Explicit Java 21 configuration in all Maven compiler plugins
- **Check:** `.mvn/jvm.config` file sets global Java 21

### **3. Database Connection Issues**
- **Check:** MySQL is running on port 3306
- **Check:** Databases exist: `thriftkaro_user_db`, `thriftkaro_product_db`, `thriftkaro_cart_db`
- **Check:** Credentials in `application.properties`

### **4. Service Discovery Issues**
- **Check:** Eureka Server is running on port 8761
- **Check:** All services register with Eureka
- **Check:** `http://localhost:8761` shows all services

### **5. JWT Token Issues**
- **Check:** User Service is running and accessible
- **Check:** JWT secret matches across services
- **Check:** Token format: `Bearer <token>`

---

## 📋 **Service Startup Order**

1. **Eureka Server** (Port 8761) - Service Discovery
2. **User Service** (Port 8080) - Authentication
3. **Product Service** (Port 8081) - Product Management
4. **Cart Service** (Port 8083) - Shopping Cart

---

## 🧪 **Testing Checklist**

### **Pre-Test Verification:**
- [ ] Java 21 installed and configured
- [ ] MySQL running with correct databases
- [ ] All services start without errors
- [ ] Eureka dashboard shows all services

### **API Testing:**
- [ ] Register user via User Service
- [ ] Login and get JWT token
- [ ] Add products via Product Service
- [ ] Create cart via Cart Service
- [ ] Add items to cart
- [ ] View cart contents

---

## 🛠️ **Manual Service Start Commands**

### **Eureka Server:**
```bash
cd EurekaServer
mvn clean compile spring-boot:run
```

### **User Service:**
```bash
cd UserService
mvn clean compile spring-boot:run
```

### **Product Service:**
```bash
cd ProductService
mvn clean compile spring-boot:run
```

### **Cart Service:**
```bash
cd cart
mvn clean compile spring-boot:run
```

---

## 📞 **If Issues Persist**

1. **Check Logs:** Look for specific error messages in console output
2. **Verify Ports:** Ensure no port conflicts (8080, 8081, 8083, 8761)
3. **Database:** Verify MySQL connection and database creation
4. **Dependencies:** Run `mvn clean install` in each service directory
5. **Java Version:** Ensure JAVA_HOME points to Java 21

---

## ✅ **Success Indicators**

- All services start without compilation errors
- Eureka dashboard shows 4 registered services
- API endpoints respond correctly
- JWT authentication works across services
- Cart operations integrate with Product Service


