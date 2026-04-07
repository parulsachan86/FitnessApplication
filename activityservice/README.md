# Activity Service

A Spring Boot microservice for tracking and managing user fitness activities. Part of the Fitness Application ecosystem with MongoDB persistence and RabbitMQ message publishing capabilities.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [API Endpoints](#api-endpoints)


## 🎯 Overview

The Activity Service is responsible for managing user fitness activities within the Fitness Application. It provides endpoints to create, retrieve, and list activities. The service integrates with MongoDB for data persistence and publishes activity events to RabbitMQ for consumption by the AI Service, enabling asynchronous recommendation generation.

**Service Details**:
- **Port**: 8082
- **Base URL**: `http://localhost:8082`
- **API Base Path**: `/api/activities`
- **Database**: MongoDB (activity_database)
- **Message Broker**: RabbitMQ
- **Registry**: Eureka Service Discovery

## ✨ Features

- Create and log user activities
- Retrieve activities by user ID
- Get individual activity details
- MongoDB document-based storage
- RabbitMQ event publishing for activity creation
- Asynchronous processing with message queue
- User validation before activity creation
- Activity type support (RUNNING, CYCLING, SWIMMING, etc.)
- Metrics tracking (calories, duration, heart rate)
- Automatic timestamps (created_at, updated_at)
- Health check endpoints

## 🛠️ Technology Stack

- **Spring Boot**: 4.0.4
- **Spring Data MongoDB**: NoSQL data access
- **MongoDB**: 7 (Database)
- **RabbitMQ**: Message brokering (AMQP)
- **Spring AMQP**: AMQP integration
- **Lombok**: Boilerplate reduction
- **Spring Cloud**: Service discovery and configuration
- **Spring Boot Actuator**: Health monitoring
- **Spring WebFlux**: Reactive web support

## 📡 API Endpoints

### 1. Add Activity

**Endpoint**: `POST /api/activities`

**Description**: Create a new activity record for a user

**Request Headers**:
```
Content-Type: application/json
```

**Request Body**:
```json
{
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "type": "RUNNING",
  "duration": 30,
  "caloriesBurned": 300,
  "startTime": "2026-04-07T09:00:00",
  "additionalMetrics": {
    "distance": 5.0,
    "avgHeartRate": 140,
    "avgPace": "6:00 min/km"
  }
}
```

**Response** (200 OK):
```json
{
  "id": "activity-507f1f77bcf86cd799439011",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "type": "RUNNING",
  "duration": 30,
  "caloriesBurned": 300,
  "startTime": "2026-04-07T09:00:00",
  "createdAt": "2026-04-07T10:30:00",
  "updatedAt": "2026-04-07T10:30:00",
  "additionalMetrics": {
    "distance": 5.0,
    "avgHeartRate": 140,
    "avgPace": "6:00 min/km"
  }
}
```

**Error Responses**:

- **400 Bad Request**: Invalid request body
```json
{
  "timestamp": "2026-04-07T10:30:00",
  "status": 400,
  "error": "Invalid activity request"
}
```

- **404 Not Found**: User does not exist
```json
{
  "timestamp": "2026-04-07T10:30:00",
  "status": 404,
  "error": "User does not exist with user_id: invalid_id"
}
```

**Notes**:
- User must exist before creating activity
- Activity events are published to RabbitMQ
- RabbitMQ publication failures are logged but don't fail the request

### 2. Get All Activities

**Endpoint**: `GET /api/activities`

**Description**: Retrieve all activities for a specific user

**Request Headers**:
```
X-Id: 123e4567-e89b-12d3-a456-426614174000
```

**Path Parameters**:
- `X-Id` (Header, Required): User ID

**Response** (200 OK):
```json
[
  {
    "id": "activity-507f1f77bcf86cd799439011",
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "type": "RUNNING",
    "duration": 30,
    "caloriesBurned": 300,
    "startTime": "2026-04-07T09:00:00",
    "createdAt": "2026-04-07T10:30:00",
    "updatedAt": "2026-04-07T10:30:00",
    "additionalMetrics": {
      "distance": 5.0,
      "avgHeartRate": 140
    }
  },
  {
    "id": "activity-507f1f77bcf86cd799439012",
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "type": "CYCLING",
    "duration": 45,
    "caloriesBurned": 400,
    "startTime": "2026-04-06T17:30:00",
    "createdAt": "2026-04-06T18:45:00",
    "updatedAt": "2026-04-06T18:45:00",
    "additionalMetrics": {
      "distance": 15.0,
      "avgSpeed": 20
    }
  }
]
```

**Empty Response** (200 OK):
```json
[]
```

### 3. Get Activity Details

**Endpoint**: `GET /api/activities/{activityId}`

**Description**: Retrieve details of a specific activity

**Path Parameters**:
- `activityId` (String, Required): The MongoDB ObjectId of the activity

**Response** (200 OK):
```json
{
  "id": "activity-507f1f77bcf86cd799439011",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "type": "RUNNING",
  "duration": 30,
  "caloriesBurned": 300,
  "startTime": "2026-04-07T09:00:00",
  "createdAt": "2026-04-07T10:30:00",
  "updatedAt": "2026-04-07T10:30:00",
  "additionalMetrics": {
    "distance": 5.0,
    "avgHeartRate": 140
  }
}
```

**Error Responses**:

- **404 Not Found**: Activity does not exist
```json
{
  "timestamp": "2026-04-07T10:30:00",
  "status": 404,
  "error": "Activity not present in the database with id: invalid_id"
}
```
