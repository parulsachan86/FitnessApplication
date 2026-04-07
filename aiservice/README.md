# AI Service

An intelligent Spring Boot microservice that generates AI-powered fitness recommendations based on user activities and data. Integrates with Gemini API for advanced analysis and provides personalized insights to users.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [API Endpoints](#api-endpoints)


## 🎯 Overview

The AI Service is responsible for generating intelligent fitness recommendations by analyzing user activities and health data. It consumes activity events from RabbitMQ, processes them using the Gemini API for AI-powered analysis, and stores recommendations in MongoDB. The service provides REST endpoints to retrieve personalized recommendations for users and specific activities.

**Service Details**:
- **Port**: 8083
- **Base URL**: `http://localhost:8083`
- **API Base Path**: `/api/recommendation`
- **Database**: MongoDB (ai_database)
- **Message Broker**: RabbitMQ (Consumer)
- **AI Provider**: Google Gemini API
- **Registry**: Eureka Service Discovery

## ✨ Features

- AI-powered fitness recommendations using Gemini API
- Asynchronous event processing from RabbitMQ
- User-based recommendation retrieval
- Activity-specific analysis and suggestions
- Recommendation persistence to MongoDB
- Real-time recommendation generation
- Advanced metrics analysis
- Health trend predictions
- Personalized improvement suggestions
- Performance tracking over time

## 🛠️ Technology Stack

- **Spring Boot**: 4.0.4
- **Spring Data MongoDB**: NoSQL data access
- **MongoDB**: 7 (Database)
- **RabbitMQ**: Message consumption (AMQP)
- **Spring AMQP**: Async message processing
- **Gemini API**: AI analysis and recommendations
- **Lombok**: Boilerplate reduction
- **Spring Cloud**: Service discovery
- **WebClient**: HTTP client for API calls
- **Spring Boot Actuator**: Health monitoring

## 📡 API Endpoints

### 1. Get User Recommendations

**Endpoint**: `GET /api/recommendation/user/{userId}`

**Description**: Retrieve all recommendations for a specific user

**Path Parameters**:
- `userId` (String, Required): The unique identifier of the user

**Response** (200 OK):
```json
[
  {
    "id": "rec-507f1f77bcf86cd799439011",
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "recommendation": "Increase your running frequency to 4 times per week for optimal cardiovascular health. Current pattern shows 2-3 runs weekly.",
    "analysis": {
      "strengthAreas": ["Good cardio endurance", "Consistent weekly routine"],
      "improvementAreas": ["Increase running frequency", "Add strength training"],
      "trend": "IMPROVING",
      "score": 7.5
    },
    "type": "GENERAL",
    "aiModel": "GEMINI",
    "createdAt": "2026-04-07T10:30:00",
    "updatedAt": "2026-04-07T10:30:00"
  },
  {
    "id": "rec-507f1f77bcf86cd799439012",
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "recommendation": "Your cycling activities show great progression. Consider adding interval training to improve performance.",
    "analysis": {
      "strengthAreas": ["Consistent distance", "Good average pace"],
      "improvementAreas": ["Add high-intensity intervals"],
      "trend": "STABLE",
      "score": 8.0
    },
    "type": "ACTIVITY_SPECIFIC",
    "aiModel": "GEMINI",
    "createdAt": "2026-04-06T15:00:00",
    "updatedAt": "2026-04-06T15:00:00"
  }
]
```

**Empty Response** (200 OK):
```json
[]
```

**Error Response**:
```json
{
  "timestamp": "2026-04-07T10:30:00",
  "status": 500,
  "error": "Unable to process recommendations"
}
```

### 2. Get Activity Recommendation

**Endpoint**: `GET /api/recommendation/activity/{activityId}`

**Description**: Get AI-generated recommendation for a specific activity

**Path Parameters**:
- `activityId` (String, Required): The MongoDB ObjectId of the activity

**Response** (200 OK):
```json
{
  "id": "rec-507f1f77bcf86cd799439013",
  "activityId": "activity-507f1f77bcf86cd799439011",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "recommendation": "Great 5K run! Your pace of 6:00 min/km shows good consistency. Try pushing for 5:45 min/km on your next run to build speed.",
  "analysis": {
    "strengthAreas": ["Consistent pace", "Good heart rate control"],
    "improvementAreas": ["Increase intensity", "Work on speed intervals"],
    "trend": "IMPROVING",
    "score": 7.8,
    "calorieEfficiency": "High",
    "timeOfDay": "Morning runs are optimal for you"
  },
  "type": "ACTIVITY_SPECIFIC",
  "performanceMetrics": {
    "paceRating": "Good",
    "heartRateRating": "Optimal",
    "durationRating": "Excellent",
    "efficiencyScore": 8.2
  },
  "aiModel": "GEMINI",
  "createdAt": "2026-04-07T09:15:00",
  "updatedAt": "2026-04-07T09:15:00"
}
```

**Error Response**:
```json
{
  "timestamp": "2026-04-07T10:30:00",
  "status": 404,
  "error": "Activity or recommendation not found"
}
```
