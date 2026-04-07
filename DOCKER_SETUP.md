# Fitness Application - Docker Setup

## Overview

This docker-compose configuration orchestrates all microservices for the Fitness Application with a proper startup order:

1. **Infrastructure Services** (Database, Message Broker, Auth)
   - PostgreSQL (User Service DB)
   - MongoDB (Activity Service DB)
   - RabbitMQ (Message Broker)
   - Keycloak (Authentication)

2. **Microservices** (with startup order enforced)
   - **Eureka Server** (Service Discovery) - Starts first
   - **Config Server** (Configuration Management) - Starts after Eureka
   - **API Gateway** - Starts after Config Server
   - **User Service** - Starts after Config Server
   - **Activity Service** - Starts after Config Server
   - **AI Service** - Starts after Config Server

## Prerequisites

1. Install Docker and Docker Compose
   ```bash
   docker --version
   docker-compose --version
   ```

2. Ensure `.env` file exists in the root directory with required variables:
   ```env
   POSTGRES_USERNAME=postgres
   POSTGRES_PASSWORD=your_password
   MONGO_USERNAME=admin
   MONGO_PASSWORD=your_password
   KEYCLOAK_ADMIN=admin
   KEYCLOAK_ADMIN_PASSWORD=your_password
   ```

3. All services must be built first (Maven JAR files must exist in target directories)

## Building Services

Build all microservices before running docker-compose:

```bash
# Build individual services
cd eureka && mvn clean package -DskipTests && cd ..
cd configurationserver && mvn clean package -DskipTests && cd ..
cd apigateway && mvn clean package -DskipTests && cd ..
cd userservice && mvn clean package -DskipTests && cd ..
cd activityservice && mvn clean package -DskipTests && cd ..
cd aiservice && mvn clean package -DskipTests && cd ..

# Or build all at once from root
mvn -f eureka/pom.xml clean package -DskipTests
mvn -f configurationserver/pom.xml clean package -DskipTests
mvn -f apigateway/pom.xml clean package -DskipTests
mvn -f userservice/pom.xml clean package -DskipTests
mvn -f activityservice/pom.xml clean package -DskipTests
mvn -f aiservice/pom.xml clean package -DskipTests
```

## Running Docker Compose

### Start all services:
```bash
docker-compose up -d
```

### View logs:
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f eureka
docker-compose logs -f config-server
docker-compose logs -f api-gateway
```

### Check service status:
```bash
docker-compose ps
```

### Stop all services:
```bash
docker-compose down
```

### Stop services and remove volumes:
```bash
docker-compose down -v
```

## Service URLs

Once all services are running:

| Service | URL | Port |
|---------|-----|------|
| Eureka Server | http://localhost:8761 | 8761 |
| Config Server | http://localhost:8888 | 8888 |
| API Gateway | http://localhost:9090 | 9090 |
| User Service | http://localhost:8081 | 8081 |
| Activity Service | http://localhost:8082 | 8082 |
| AI Service | http://localhost:8083 | 8083 |
| PostgreSQL | localhost:5432 | 5432 |
| MongoDB | localhost:27017 | 27017 |
| RabbitMQ | http://localhost:15672 | 15672 |
| Keycloak | http://localhost:8080 | 8080 |

## Startup Order Enforcement

The docker-compose configuration enforces the following startup order using `depends_on` with health checks:

```
Infrastructure (in parallel)
  ├── PostgreSQL
  ├── MongoDB
  ├── RabbitMQ
  └── Keycloak

Microservices (sequential)
  ├── Eureka (no dependencies)
  │
  ├── Config Server (waits for Eureka to be healthy)
  │
  └── Other Services (wait for Config Server to be healthy)
      ├── API Gateway
      ├── User Service
      ├── Activity Service
      └── AI Service
```

## Health Checks

Each service includes health checks to ensure proper startup sequence:

- **Eureka**: HTTP GET to `/`
- **Config Server**: HTTP GET to `/actuator/health`
- **Databases**: Native health checks
- **RabbitMQ**: RabbitMQ diagnostics ping

## Troubleshooting

### Services not starting:
```bash
# Check logs
docker-compose logs

# Rebuild images
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

### Port conflicts:
If ports are already in use, modify the port mappings in `docker-compose.yml`:
```yaml
ports:
  - "8761:8761"  # Change first port to available port
```

### Network issues:
Services communicate via the `fitness_network` Docker network. Ensure no firewall issues:
```bash
# Check network
docker network ls
docker network inspect fitness_network
```

### Database connectivity:
Services use internal Docker DNS names (e.g., `db`, `mongodb`, `rabbitmq`). Connection strings are configured automatically.

## Environment Variables

All services inherit environment variables from the `.env` file and docker-compose configuration:

- `POSTGRES_USERNAME` - PostgreSQL user
- `POSTGRES_PASSWORD` - PostgreSQL password
- `MONGO_USERNAME` - MongoDB admin user
- `MONGO_PASSWORD` - MongoDB admin password
- `KEYCLOAK_ADMIN` - Keycloak admin user
- `KEYCLOAK_ADMIN_PASSWORD` - Keycloak admin password
- `JAVA_OPTS` - JVM options for all services

## Notes

- All services restart automatically if they crash (`restart: always`)
- Data is persisted in Docker volumes
- Memory limit: 512MB max, 256MB min for Java services
- Java 21 is used for all microservices

