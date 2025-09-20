# 🚀 Setup Guide - E-Commerce Backend

## 📋 Prerequisites Setup

Before running the application, you need to set up the following services:

### 1. **PostgreSQL Database**

```sql
-- Create database and user
CREATE DATABASE ecommerce_db;
CREATE USER ecommerce_user WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE ecommerce_db TO ecommerce_user;
```

### 2. **Redis Server**

```bash
# Using Docker (Recommended)
docker run -d -p 6379:6379 --name redis-ecommerce redis:alpine

# Or install locally
# Windows: Download from https://redis.io/download
# macOS: brew install redis && brew services start redis
# Linux: sudo apt-get install redis-server && sudo systemctl start redis
```

### 3. **Kafka Server (Optional - for async features)**

```bash
# Using Docker Compose
version: '3.8'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```

## ⚙️ Configuration Setup

### 1. **Create your application.properties**

```bash
# Copy the demo file and customize it
cp src/main/resources/application-demo.properties src/main/resources/application.properties

# Edit the file with your actual values:
# - Database credentials
# - Redis connection details
# - JWT secret key (generate a secure 256-bit key)
# - Email SMTP settings
```

### 2. **Generate JWT Secret Key**

```bash
# Generate a secure 256-bit key (required for JWT)
openssl rand -base64 32

# Or use online generator: https://generate-random.org/api-key-generator
# Or use: head -c 32 /dev/urandom | base64
```

### 3. **Email Configuration (For notifications)**

```properties
# For Gmail:
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-specific-password  # Generate from Google Account settings

# For other providers, update host and port accordingly
```

## 🏃‍♂️ Running the Application

### 1. **Clone and Setup**

```bash
git clone <your-repo-url>
cd ECB
```

### 2. **Configure**

```bash
# Copy demo config and update with your values
cp src/main/resources/application-demo.properties src/main/resources/application.properties

# Edit application.properties with your database, Redis, and other settings
```

### 3. **Build and Run**

```bash
# Build the project
mvn clean compile

# Run the application
mvn spring-boot:run

# Or run the JAR file
mvn clean package
java -jar target/ECB-0.0.1-SNAPSHOT.jar
```

### 4. **Verify Setup**

```bash
# Check if application is running
curl http://localhost:8080/products

# Access Swagger UI
open http://localhost:8080/swagger-ui.html
```

## 🧪 Testing the API

### 1. **Register a User**

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

### 2. **Login and Get Token**

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

### 3. **Create Admin User (Direct DB Insert)**

```sql
-- Insert admin user directly in database
INSERT INTO users (name, email, password, role, enabled, created_at, updated_at)
VALUES (
  'Admin User',
  'admin@example.com',
  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',  -- password: password
  'ADMIN',
  true,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
);
```

## 🔧 Environment Profiles

### Development

```bash
# Run with dev profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Production

```bash
# Run with prod profile
java -jar target/ECB-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## 📊 Monitoring and Health

```bash
# Application health
curl http://localhost:8080/actuator/health

# Application info
curl http://localhost:8080/actuator/info
```

## ⚠️ Security Notes

1. **Never commit real credentials to Git**
2. **Use strong, unique JWT secret keys**
3. **Enable HTTPS in production**
4. **Use environment variables for sensitive data**
5. **Regularly update dependencies**

## 📞 Troubleshooting

### Common Issues:

1. **Database Connection Failed**: Check PostgreSQL is running and credentials are correct
2. **Redis Connection Failed**: Ensure Redis server is running on port 6379
3. **JWT Token Invalid**: Verify JWT secret key is properly configured
4. **Port Already in Use**: Change server.port in application.properties

### Logs Location:

- Application logs: Console output
- Spring logs: Set `logging.level.org.springframework=DEBUG` for detailed logs

---

**Need help?** Check the [README.md](README.md) for detailed project documentation.
