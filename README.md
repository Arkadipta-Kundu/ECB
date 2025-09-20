# 🛒 E-Commerce Backend Clone

A comprehensive, production-ready e-commerce backend system built with **Spring Boot 3**, **Java 21**, and modern technologies. This project implements a full-featured online marketplace backend supporting customers and admins with JWT authentication, product management, shopping cart, orders, and payment simulation.

## 🚀 Features Implemented

### ✅ **Core Features Completed**

#### 🔐 **Authentication & Authorization**

- **JWT Token-based Authentication** with access and refresh tokens
- **Role-based Authorization** (Customer, Admin)
- **Secure Password Storage** with BCrypt hashing
- **User Registration & Login** endpoints
- **Token Refresh** mechanism

#### 📦 **Product Management**

- **CRUD Operations** for products (Admin only)
- **Advanced Search & Filtering** by category, price, rating, name
- **Pagination & Sorting** for product listings
- **Product Categories** management
- **Stock Management** with real-time validation
- **Redis Caching** for improved performance

#### 🛒 **Shopping Cart**

- **Add/Remove/Update** items in cart
- **Real-time Stock Validation**
- **Cart Persistence** across sessions
- **Auto-cleanup** of unavailable/out-of-stock items
- **Cart Calculations** (subtotal, total, item count)

#### ⚡ **Performance & Caching**

- **Redis Integration** for product catalog caching
- **Optimized Queries** with JPA specifications
- **Pagination** for large datasets

#### 📑 **API Documentation**

- **Swagger/OpenAPI 3** integration
- **Interactive API Documentation** at `/swagger-ui.html`
- **JWT Authentication** support in Swagger UI

## 🛠 **Tech Stack**

| Component             | Technology                       |
| --------------------- | -------------------------------- |
| **Backend Framework** | Spring Boot 3.5.6                |
| **Language**          | Java 21                          |
| **Database**          | PostgreSQL (Primary), H2 (Tests) |
| **Caching**           | Redis                            |
| **Authentication**    | JWT (JSON Web Tokens)            |
| **Security**          | Spring Security 6                |
| **Documentation**     | Swagger/OpenAPI 3                |
| **Build Tool**        | Maven                            |
| **Testing**           | JUnit 5, Mockito, Testcontainers |

## 📋 **Prerequisites**

- **Java 21** or higher
- **Maven 3.6+**
- **PostgreSQL 12+**
- **Redis Server**
- **Git**

## 🚀 **Quick Start**

### 1. **Clone the Repository**

```bash
git clone <repository-url>
cd ECB
```

### 2. **Database Setup**

```sql
-- Create PostgreSQL database
CREATE DATABASE ecommerce_db;
CREATE USER ecommerce_user WITH PASSWORD 'ecommerce_pass';
GRANT ALL PRIVILEGES ON DATABASE ecommerce_db TO ecommerce_user;
```

### 3. **Start Redis Server**

```bash
# Using Docker
docker run -d -p 6379:6379 redis:alpine

# Or install locally and start
redis-server
```

### 4. **Configure Application**

```bash
# Copy the demo configuration
cp src/main/resources/application-demo.properties src/main/resources/application.properties

# Update application.properties with your actual values:
# - Database credentials
# - JWT secret key (generate with: openssl rand -base64 32)
# - Email SMTP settings
```

### 5. **Build and Run**

```bash
# Build the project
mvn clean compile

# Run the application
mvn spring-boot:run
```

### 6. **Access the Application**

- **API Base URL**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Docs**: `http://localhost:8080/api-docs`

> ⚠️ **Security Note**: This repository contains demo configuration files only. Real credentials are gitignored. See [SETUP.md](SETUP.md) for detailed configuration instructions.

## 📚 **API Endpoints**

### 🔐 **Authentication**

| Method | Endpoint         | Description       | Access |
| ------ | ---------------- | ----------------- | ------ |
| `POST` | `/auth/register` | Register new user | Public |
| `POST` | `/auth/login`    | User login        | Public |
| `POST` | `/auth/refresh`  | Refresh JWT token | Public |

### 📦 **Products**

| Method   | Endpoint               | Description                  | Access |
| -------- | ---------------------- | ---------------------------- | ------ |
| `GET`    | `/products`            | Get all products (paginated) | Public |
| `GET`    | `/products/{id}`       | Get product by ID            | Public |
| `GET`    | `/products/search`     | Search products with filters | Public |
| `GET`    | `/products/categories` | Get all categories           | Public |
| `POST`   | `/products`            | Create new product           | Admin  |
| `PUT`    | `/products/{id}`       | Update product               | Admin  |
| `DELETE` | `/products/{id}`       | Delete product               | Admin  |
| `PATCH`  | `/products/{id}/stock` | Update product stock         | Admin  |

### 🛒 **Shopping Cart**

| Method   | Endpoint                | Description               | Access        |
| -------- | ----------------------- | ------------------------- | ------------- |
| `GET`    | `/cart`                 | Get user's cart           | Authenticated |
| `POST`   | `/cart/add`             | Add item to cart          | Authenticated |
| `PUT`    | `/cart/update/{itemId}` | Update cart item quantity | Authenticated |
| `DELETE` | `/cart/remove/{itemId}` | Remove item from cart     | Authenticated |
| `DELETE` | `/cart/clear`           | Clear entire cart         | Authenticated |

## 🔧 **Configuration**

### **Application Properties**

Key configuration options in `application.properties`:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
spring.jpa.hibernate.ddl-auto=update

# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379

# JWT Configuration
jwt.secret=mySecretKey12345678901234567890123456789012345678901234567890
jwt.expiration=86400000
jwt.refresh-expiration=604800000

# Swagger Configuration
springdoc.swagger-ui.path=/swagger-ui.html
```

## 🗂 **Project Structure**

```
src/main/java/org/arkadipta/ecb/
├── config/              # Configuration classes
│   ├── JpaConfig.java
│   ├── RedisConfig.java
│   ├── SecurityConfig.java
│   └── SwaggerConfig.java
├── controller/          # REST Controllers
│   ├── AuthController.java
│   ├── CartController.java
│   └── ProductController.java
├── dto/                 # Data Transfer Objects
│   ├── auth/
│   ├── cart/
│   └── product/
├── exception/           # Exception handling
│   ├── GlobalExceptionHandler.java
│   ├── ErrorResponse.java
│   └── Custom exceptions...
├── model/              # JPA Entities
│   ├── BaseEntity.java
│   ├── User.java
│   ├── Product.java
│   ├── Cart.java
│   ├── CartItem.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── Payment.java
│   └── enums/
├── repository/         # Data Access Layer
│   ├── UserRepository.java
│   ├── ProductRepository.java
│   ├── CartRepository.java
│   └── CartItemRepository.java
├── security/           # Security components
│   ├── JwtUtils.java
│   └── JwtAuthenticationFilter.java
└── service/            # Business Logic
    ├── AuthService.java
    ├── CartService.java
    ├── ProductService.java
    └── UserDetailsServiceImpl.java
```

## 🧪 **Testing**

### **Run Tests**

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ProductServiceTest

# Run tests with coverage
mvn test jacoco:report
```

## 🔮 **Upcoming Features**

### 🚧 **In Development**

- **Order Management System** with status workflow
- **Payment Simulation** with mock gateway
- **Kafka Integration** for async processing
- **Email Notifications** for order updates
- **Comprehensive Test Suite**

### 📈 **Future Enhancements**

- **Real Payment Gateway** integration (Stripe/PayPal)
- **Product Reviews & Ratings**
- **Wishlist Functionality**
- **Inventory Analytics**
- **Admin Dashboard**
- **Mobile API Optimization**

## 📊 **Database Schema**

### **Core Tables**

- **users**: User accounts with role-based access
- **products**: Product catalog with categories and stock
- **carts**: User shopping carts
- **cart_items**: Items in shopping carts
- **orders**: Order records with status tracking
- **order_items**: Individual items in orders
- **payments**: Payment transactions

## 🔒 **Security Features**

- **JWT Authentication** with secure token generation
- **BCrypt Password Hashing**
- **Role-based Authorization** (RBAC)
- **CORS Configuration** for cross-origin requests
- **Input Validation** on all endpoints
- **SQL Injection Prevention** with JPA
- **XSS Protection** with proper serialization

## 🎯 **Getting Started for Development**

### **Sample API Calls**

#### Register a User

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

#### Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

#### Add Product (Admin)

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Smartphone",
    "description": "Latest smartphone with advanced features",
    "price": 699.99,
    "stock": 50,
    "category": "Electronics"
  }'
```

## 🤝 **Contributing**

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 **License**

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
