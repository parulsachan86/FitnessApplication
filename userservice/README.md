# User Service

A Spring Boot microservice responsible for user management, registration, and authentication. Part of the Fitness Application ecosystem.

## 🎯 Overview

The User Service is a microservice that manages user accounts, registration, and validation within the Fitness Application. It provides endpoints for user registration, profile retrieval, and user existence validation. The service uses PostgreSQL for data persistence and integrates with the Eureka service registry for dynamic service discovery.

**Service Details**:
- **Port**: 8081
- **Base URL**: `http://localhost:8081`
- **API Base Path**: `/api/users`
- **Database**: PostgreSQL (fitness_user_db)
- **Registry**: Eureka Service Discovery

## ✨ Features

- User registration with validation
- User profile management
- User existence validation
- Email uniqueness enforcement
- Password hashing and storage
- Audit timestamps (created_at, updated_at)
- Role-based user management
- RESTful API architecture

## 🛠️ Technology Stack

- **Spring Boot**: 4.0.4
- **Spring Data JPA**: ORM with Hibernate
- **PostgreSQL**: 15 (Database)
- **Lombok**: Boilerplate reduction
- **Spring Cloud**: Service discovery and configuration
- **Spring Validation**: Request validation
- **Eureka Client**: Service registration
- **Spring Boot Actuator**: Health monitoring

## 📡 API Endpoints

### 1. Register User

**Endpoint**: `POST /api/users/register`

**Description**: Register a new user with validation

**Request Headers**:
```
Content-Type: application/json
```

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response** (200 OK):
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "email": "user@example.com",
  "password": "SecurePassword123",
  "firstName": "John",
  "lastName": "Doe",
  "createdAt": "2026-04-07T10:30:00",
  "updateAt": "2026-04-07T10:30:00"
}
```

**Error Responses**:

- **400 Bad Request**: Validation failed
```json
{
  "timestamp": "2026-04-07T10:30:00",
  "status": 400,
  "error": "Email is required",
  "path": "/api/users/register"
}
```

- **409 Conflict**: Email already exists
```json
{
  "timestamp": "2026-04-07T10:30:00",
  "status": 409,
  "error": "Email Already exist",
  "path": "/api/users/register"
}
```

**Validation Rules**:
- Email: Required, valid email format
- Password: Required, minimum 6 characters
- First Name: Optional
- Last Name: Optional

### 2. Get User Details

**Endpoint**: `GET /api/users/{userId}`

**Description**: Retrieve details of a specific user

**Path Parameters**:
- `userId` (String, Required): The unique identifier of the user

**Response** (200 OK):
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "email": "user@example.com",
  "password": "SecurePassword123",
  "firstName": "John",
  "lastName": "Doe",
  "createdAt": "2026-04-07T10:30:00",
  "updateAt": "2026-04-07T10:30:00"
}
```

**Error Responses**:

- **404 Not Found**: User does not exist
```json
{
  "timestamp": "2026-04-07T10:30:00",
  "status": 404,
  "error": "User does not exist with userId:123e4567",
  "path": "/api/users/123e4567"
}
```

### 3. Validate User

**Endpoint**: `GET /api/users/{userId}/validate`

**Description**: Check if a user exists in the system

**Path Parameters**:
- `userId` (String, Required): The unique identifier of the user

**Response** (200 OK):
```json
true
```

or

```json
false
```

**Example Responses**:

Success - User exists:
```bash
GET http://localhost:8081/api/users/123e4567-e89b-12d3-a456-426614174000/validate
Response: true
```

Success - User does not exist:
```bash
GET http://localhost:8081/api/users/non-existing-id/validate
Response: false
```
