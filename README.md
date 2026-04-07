# Fitness Application - Microservices Architecture

A comprehensive, scalable fitness tracking and recommendation system built with Spring Boot microservices architecture. The application provides user management, activity tracking, AI-powered recommendations, and centralized API gateway management.

## 📋 Table of Contents

- [Architecture Overview](#architecture-overview)
- [System Requirements](#system-requirements)
- [Technologies Stack](#technologies-stack)
- [Services Overview](#services-overview)
- [API Endpoints](#api-endpoints)
- [Installation & Setup](#installation--setup)
- [Environment Configuration](#environment-configuration)
- [Docker Deployment](#docker-deployment)
- [Service Communication](#service-communication)
- [Database Configuration](#database-configuration)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)

## 🏗️ Architecture Overview

The Fitness Application uses a microservices architecture with the following components:

```
┌─────────────┐
│   Keycloak  │ (Authentication & Authorization)
└─────────────┘
       │
       └──────────────────────────┐
                                 │
┌─────────────────────────────────────────────────────┐
│          API Gateway (Port 9090)                    │
├──────────────────────────────────────────────────────┤
│ Routes requests to appropriate microservices        │
└─────────────┬───────────────┬───────────────┬────────┘
              │               │               │
       ┌──────▼────┐   ┌──────▼────┐  ┌──────▼────┐
       │ User Svc  │   │Activity   │  │ AI Svc    │
       │ (8081)    │   │ Svc(8082) │  │ (8083)    │
       └────┬──────┘   └──────┬────┘  └──────┬────┘
            │                 │              │
       ┌────▼────┐      ┌─────▼────┐  ┌────▼────┐
       │PostgreSQL│     │ MongoDB   │  │ MongoDB  │
       │          │     │           │  │          │
       └──────────┘     └───────────┘  └──────────┘
            
┌────────────────────────────────────────────────┐
│  Service Discovery (Eureka - Port 8761)       │
├────────────────────────────────────────────────┤
│  Config Server (Port 8888)                     │
│  RabbitMQ Message Broker (Port 5672)           │
└────────────────────────────────────────────────┘
```

## 🖥️ System Requirements

- **Java**: JDK 21 or higher
- **Docker**: 20.10+
- **Docker Compose**: 1.29+
- **Maven**: 3.8+
- **Memory**: Minimum 4GB RAM for Docker containers
- **Disk Space**: Minimum 5GB for all services and databases

## 🛠️ Technologies Stack

### Core Framework
- **Spring Boot**: 4.0.4
- **Spring Cloud**: 2025.1.1

### Microservices Components
- **Eureka**: Service Discovery
- **Spring Cloud Config**: Centralized Configuration
- **Spring Cloud Gateway**: API Gateway
- **Spring AMQP**: Message Brokering with RabbitMQ

### Databases
- **PostgreSQL**: 15 (User data)
- **MongoDB**: 7 (Activity & AI data)

### Authentication
- **Keycloak**: OpenID Connect provider

### Message Queue
- **RabbitMQ**: 3-management (Asynchronous messaging)

### Other Technologies
- **Lombok**: Reduce boilerplate code
- **JPA/Hibernate**: ORM for PostgreSQL
- **Spring Data MongoDB**: MongoDB integration
- **REST APIs**: JSON-based communication

## 📦 Services Overview

### 1. **Eureka Service Discovery** (Port 8761)
- Service registry and discovery
- Health monitoring
- Dynamic service registration

### 2. **Configuration Server** (Port 8888)
- Centralized configuration management
- Service-specific property files
- Dynamic property updates

### 3. **API Gateway** (Port 9090)
- Single entry point for all client requests
- Request routing to appropriate microservices
- Load balancing and request filtering

### 4. **User Service** (Port 8081)
- User registration and authentication
- User profile management
- User validation

### 5. **Activity Service** (Port 8082)
- Activity tracking and logging
- Activity retrieval
- Activity persistence to MongoDB
- RabbitMQ message publishing

### 6. **AI Service** (Port 8083)
- AI-powered recommendations
- Activity analysis using Gemini API
- Recommendation persistence

### 7. **Keycloak** (Port 8080) - InProgress
- OAuth2/OpenID Connect provider
- User identity management
- Token generation and validation

## 🔌 API Endpoints

### User Service Endpoints

**Base URL**: `http://localhost:8081/api/users`

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/register` | Register a new user | RegisterRequest | UserResponse |
| GET | `/{userId}` | Get user details | - | UserResponse |
| GET | `/{userId}/validate` | Validate if user exists | - | Boolean |

### Activity Service Endpoints

**Base URL**: `http://localhost:8082/api/activities`

| Method | Endpoint | Description | Headers | Request Body | Response |
|--------|----------|-------------|---------|--------------|----------|
| POST | `/` | Add new activity | - | ActivityRequest | ActivityResponse |
| GET | `/` | Get all activities | X-Id (userId) | - | List[ActivityResponse] |
| GET | `/{activityId}` | Get specific activity | - | - | ActivityResponse |

### AI Service Endpoints

**Base URL**: `http://localhost:8083/api/recommendation`

| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| GET | `/user/{userId}` | Get user recommendations | List[Recommendation] |
| GET | `/activity/{activityId}` | Get activity recommendation | Recommendation |

[//]: # (### Health Check Endpoints)

[//]: # ()
[//]: # (| Service | Endpoint | Port |)

[//]: # (|---------|----------|------|)

[//]: # (| User Service | `GET /actuator/health` | 8081 |)

[//]: # (| Activity Service | `GET /actuator/health` | 8082 |)

[//]: # (| AI Service | `GET /actuator/health` | 8083 |)

[//]: # (| API Gateway | `GET /actuator/health` | 9090 |)

[//]: # (| Config Server | `GET /actuator/health` | 8888 |)

[//]: # (| Eureka | `GET /` | 8761 |)

## 🚀 Installation & Setup

### Prerequisites
Ensure you have the following installed:
```bash
java -version        # Should be JDK 21+
docker --version     # Should be 20.10+
docker-compose --version  # Should be 1.29+
```

### Local Development Setup

1. **Clone the Repository**
   ```bash
   cd FitnessApplication
   ```

2. **Configure Environment Variables**
    Replace the actual values in the .env file with your credentials and API keys.
   ```bash
   # Create or update .env file
   cat > .env << EOF
   MONGO_USERNAME=<mongo_user>
   MONGO_PASSWORD=<mongo_password>
   POSTGRES_USERNAME=<postgres_user>
   POSTGRES_PASSWORD=<postgres_pass>
   KEYCLOAK_ADMIN=<keycloak_admin_user>
   KEYCLOAK_ADMIN_PASSWORD=<keycloak_admin_pass>
   GEMINI_API_KEY=<your_gemini_api_key>
   GEMINI_API_URL=<your_gemini_api_url>
   EOF
   ```

3. **Build All Services**
   ```bash
   mvn clean install
   ```

4. **Start Services Locally** (Optional - for development without Docker)
   ```bash
   # Start each service in a separate terminal
   cd eureka && mvn spring-boot:run
   cd configurationserver && mvn spring-boot:run
   cd userservice && mvn spring-boot:run
   cd activityservice && mvn spring-boot:run
   cd aiservice && mvn spring-boot:run
   cd apigateway && mvn spring-boot:run
   ```

## 🐳 Docker Deployment

### Build and Deploy with Docker Compose

1. **Build Docker Images**
   ```bash
   docker-compose build
   ```

2. **Start All Services**
   ```bash
   docker-compose up -d
   ```

3. **Verify Services are Running**
   ```bash
   docker-compose ps
   ```
   
   Expected output:
   ```
   NAME                  STATUS
   user_db               Up (healthy)
   fitness-rabbitmq      Up (healthy)
   activityservice-db    Up (healthy)
   keycloak              Up
   eureka-server         Up (healthy)
   config-server         Up (healthy)
   user-service          Up (healthy)
   activity-service      Up (healthy)
   ai-service            Up (healthy)
   api-gateway           Up
   ```

4. **Access Services**
   - API Gateway: http://localhost:9090
   - User Service: http://localhost:8081
   - Activity Service: http://localhost:8082
   - AI Service: http://localhost:8083
   - Eureka Dashboard: http://localhost:8761
   - Config Server: http://localhost:8888
   - Keycloak: http://localhost:8080
   - RabbitMQ Management: http://localhost:15672 (guest/guest)

5. **Stop All Services**
   ```bash
   docker-compose down
   ```

6. **View Logs**
   ```bash
   docker-compose logs -f [service_name]
   docker-compose logs -f activity-service
   ```

### Service-Specific Configuration

Each service has its own configuration file in `configurationserver/src/main/resources/config/`:

- `activity-service.properties`
- `user-service.properties`
- `ai-service.properties`
- `api-gateway.yml`

## 🔄 Service Communication

### Message Flow

1. **User Registration Flow**
   ```
   Client → API Gateway → User Service → PostgreSQL
   ```

2. **Activity Creation Flow**
   ```
   Client → API Gateway → Activity Service → MongoDB
                               ↓
                           RabbitMQ → AI Service → Gemini API → MongoDB
   ```

3. **Recommendation Flow**
   ```
   Client → API Gateway → AI Service → MongoDB → Recommendation
   ```

### RabbitMQ Integration

The Activity Service publishes activity events to RabbitMQ:
- **Exchange**: `activity-exchange`
- **Routing Key**: `activity.created`
- **Queue**: `activity-queue`

The AI Service consumes these events and generates recommendations.

## 💾 Database Configuration

### PostgreSQL (User Service)

**Database**: `fitness_user_db`
**Port**: 5432

Tables:
- `users` - User account information
- `user_role` - User role assignments

### MongoDB (Activity & AI Services)

**Port**: 27017

Collections:
- `activity_database.activities` - Activity records
- `ai_database.recommendations` - AI recommendations

### RabbitMQ

**Port**: 5672
**Management UI**: http://localhost:15672
**Default Credentials**: guest/guest

## 📊 Request/Response Examples

### Register User
```bash
POST http://localhost:9090/api/users/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123",
  "firstName": "John",
  "lastName": "Doe"
}
```

Response:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "email": "user@example.com",
  "password": "SecurePass123",
  "firstName": "John",
  "lastName": "Doe",
  "createdAt": "2026-04-07T10:30:00",
  "updateAt": "2026-04-07T10:30:00"
}
```

### Add Activity
```bash
POST http://localhost:9090/api/activities
Content-Type: application/json

{
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "type": "RUNNING",
  "duration": 30,
  "caloriesBurned": 300,
  "startTime": "2026-04-07T09:00:00",
  "additionalMetrics": {
    "distance": 5.0,
    "avgHeartRate": 140
  }
}
```

Response:
```json
{
  "id": "activity-123",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "type": "RUNNING",
  "duration": 30,
  "caloriesBurned": 300,
  "startTime": "2026-04-07T09:00:00",
  "createdAt": "2026-04-07T10:35:00",
  "updatedAt": "2026-04-07T10:35:00",
  "additionalMetrics": {
    "distance": 5.0,
    "avgHeartRate": 140
  }
}
```

### Get User Recommendations
```bash
GET http://localhost:9090/api/recommendation/user/123e4567-e89b-12d3-a456-426614174000
```

Response:
```json
[
  {
    "id": "rec-001",
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "recommendation": "Increase running frequency for better cardiovascular health",
    "aiModel": "GEMINI",
    "createdAt": "2026-04-07T10:40:00"
  }
]
```

## 🔍 Troubleshooting

### Common Issues

#### 1. MongoDB Connection Fails
**Problem**: Activity Service cannot connect to MongoDB
```
Error: connect ECONNREFUSED 127.0.0.1:27017
```

**Solution**:
```bash
# Check if MongoDB is running
docker-compose ps mongodb

# Check MongoDB logs
docker-compose logs mongodb

# Verify MongoDB healthcheck
docker-compose logs | grep mongodb
```

#### 2. Service Won't Start
**Problem**: Service fails during startup
```
Error: Unable to start embedded server; nested exception...
```

**Solution**:
```bash
# Check logs
docker-compose logs [service-name]

# Verify configuration server is healthy
curl http://localhost:8888/actuator/health

# Rebuild service
docker-compose up -d --build [service-name]
```

#### 3. API Gateway Returns 404
**Problem**: Routes not found through gateway
```
{
  "timestamp": "2026-04-07T10:50:00",
  "status": 404,
  "error": "Not Found"
}
```

**Solution**:
```bash
# Check Eureka dashboard
curl http://localhost:8761/

# Verify service is registered
# Check API Gateway configuration in config server
curl http://localhost:8888/api-gateway-default.yml
```

#### 4. RabbitMQ Message Not Publishing
**Problem**: Activities not being sent to RabbitMQ
```
Error: Failed to publish activity to RabbitMQ
```

**Solution**:
```bash
# Check RabbitMQ is healthy
docker-compose ps rabbitmq

# Verify RabbitMQ credentials
docker-compose logs rabbitmq | grep password

# Check queue
curl -u guest:guest http://localhost:15672/api/queues
```

#### 5. Health Check Failing
**Problem**: Docker health check keeps failing
```
Status: unhealthy
```

**Solution**:
```bash
# Check service logs
docker-compose logs [service-name]

# Verify actuator endpoint
curl http://localhost:[port]/actuator/health

# Ensure dependencies are healthy
docker-compose ps

# Increase health check start period
# Edit docker-compose.yml start_period value
```

### Debug Commands

```bash
# View all container logs
docker-compose logs

# Follow logs for specific service
docker-compose logs -f activity-service

# Check container health
docker-compose ps

# Execute command in container
docker-compose exec activity-service curl http://localhost:8082/actuator/health

# Check network connectivity
docker-compose exec activity-service ping mongodb

# View environment variables in container
docker-compose exec activity-service printenv
```

## 📁 Project Structure

```
FitnessApplication/
├── eureka/                          # Service Discovery
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── configurationserver/             # Config Server
│   ├── src/
│   ├── pom.xml
│   ├── Dockerfile
│   └── resources/
│       └── config/                  # Service configurations
│           ├── activity-service.properties
│           ├── user-service.properties
│           ├── ai-service.properties
│           └── api-gateway.yml
├── apigateway/                      # API Gateway
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── userservice/                     # User Management Service
│   ├── src/
│   ├── pom.xml
│   ├── Dockerfile
│   └── README.md
├── activityservice/                 # Activity Tracking Service
│   ├── src/
│   ├── pom.xml
│   ├── Dockerfile
│   └── README.md
├── aiservice/                       # AI Recommendation Service
│   ├── src/
│   ├── pom.xml
│   ├── Dockerfile
│   └── README.md
├── docker-compose.yml               # Docker Compose Configuration
├── .env                            # Environment Variables
└── README.md                        # This file
```

## 🧪 Testing

### Unit Tests

Run tests for all services:
```bash
mvn clean test
```

Run tests for specific service:
```bash
cd userservice
mvn clean test
```

### Integration Tests

```bash
mvn clean verify
```

### API Testing with cURL

```bash
# User Registration
curl -X POST http://localhost:9090/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "TestPass123",
    "firstName": "Test",
    "lastName": "User"
  }'

# Get User Details
curl -X GET http://localhost:9090/api/users/[userId]

# Add Activity
curl -X POST http://localhost:9090/api/activities \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "[userId]",
    "type": "RUNNING",
    "duration": 30,
    "caloriesBurned": 300,
    "startTime": "2026-04-07T09:00:00"
  }'

# Get Activities
curl -X GET http://localhost:9090/api/activities \
  -H "X-Id: [userId]"
```

## 📝 Contributing

1. Create a feature branch: `git checkout -b feature/feature-name`
2. Commit changes: `git commit -m 'Add feature-name'`
3. Push to branch: `git push origin feature/feature-name`
4. Submit pull request


## 👥 Support

For issues and questions:
1. Check the [Troubleshooting](#troubleshooting) section
2. Review service-specific READMEs
3. Check Docker Compose logs
4. Contact the development team

---

**Last Updated**: April 7, 2026
**Version**: 1.0.0

