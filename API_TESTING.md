# API Testing Guide

## Quick Start with cURL

### 1. Register Users

**Register a Rider:**
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "rider@example.com",
    "password": "password123",
    "name": "John Rider",
    "phoneNumber": "+1234567890",
    "role": "RIDER"
  }'
```

**Register a Driver:**
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "driver@example.com",
    "password": "password123",
    "name": "Jane Driver",
    "phoneNumber": "+0987654321",
    "role": "DRIVER"
  }'
```

### 2. Login and Get Token

**Rider Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "rider@example.com",
    "password": "password123"
  }'
```
Save the `token` from response as `RIDER_TOKEN`

**Driver Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "driver@example.com",
    "password": "password123"
  }'
```
Save the `token` from response as `DRIVER_TOKEN`

### 3. Driver Setup

**Register Vehicle:**
```bash
curl -X POST http://localhost:8080/api/drivers/vehicle \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -d '{
    "plateNumber": "ABC123",
    "model": "Toyota Camry",
    "color": "Black",
    "year": 2022
  }'
```

**Go Online:**
```bash
curl -X POST http://localhost:8080/api/drivers/status/online \
  -H "Authorization: Bearer $DRIVER_TOKEN"
```

**Update Location:**
```bash
curl -X POST http://localhost:8080/api/drivers/location \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -d '{
    "latitude": 37.7749,
    "longitude": -122.4194
  }'
```

### 4. Request Trip

**Get Price Estimate:**
```bash
curl -X POST http://localhost:8080/api/pricing/estimate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $RIDER_TOKEN" \
  -d '{
    "originLatitude": 37.7749,
    "originLongitude": -122.4194,
    "destinationLatitude": 37.7849,
    "destinationLongitude": -122.4094,
    "distanceKm": 1.5
  }'
```

**Request Trip:**
```bash
curl -X POST http://localhost:8080/api/trips/request \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $RIDER_TOKEN" \
  -d '{
    "originLatitude": 37.7749,
    "originLongitude": -122.4194,
    "destinationLatitude": 37.7849,
    "destinationLongitude": -122.4094
  }'
```
Save the `id` from response as `TRIP_ID`

### 5. Trip Lifecycle

**Accept Trip (Driver):**
```bash
curl -X POST http://localhost:8080/api/trips/$TRIP_ID/accept \
  -H "Authorization: Bearer $DRIVER_TOKEN"
```

**Start Trip:**
```bash
curl -X POST http://localhost:8080/api/trips/$TRIP_ID/start \
  -H "Authorization: Bearer $DRIVER_TOKEN"
```

**Complete Trip:**
```bash
curl -X POST http://localhost:8080/api/trips/$TRIP_ID/complete \
  -H "Authorization: Bearer $DRIVER_TOKEN"
```

### 6. Rate Each Other

**Rider Rates Driver:**
```bash
curl -X POST http://localhost:8080/api/ratings/driver \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $RIDER_TOKEN" \
  -d '{
    "tripId": '$TRIP_ID',
    "rating": 5.0,
    "comment": "Excellent driver!"
  }'
```

**Driver Rates Rider:**
```bash
curl -X POST http://localhost:8080/api/ratings/rider \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -d '{
    "tripId": '$TRIP_ID',
    "rating": 5.0,
    "comment": "Great passenger!"
  }'
```

### 7. View History

**Rider Trip History:**
```bash
curl -X GET "http://localhost:8080/api/trips/rider/history?page=0&size=10" \
  -H "Authorization: Bearer $RIDER_TOKEN"
```

**Driver Trip History:**
```bash
curl -X GET "http://localhost:8080/api/trips/driver/history?page=0&size=10" \
  -H "Authorization: Bearer $DRIVER_TOKEN"
```

### 8. Analytics

**Driver Stats:**
```bash
curl -X GET http://localhost:8080/api/analytics/driver \
  -H "Authorization: Bearer $DRIVER_TOKEN"
```

**Rider Stats:**
```bash
curl -X GET http://localhost:8080/api/analytics/rider \
  -H "Authorization: Bearer $RIDER_TOKEN"
```

### 9. Health Checks

**Basic Health:**
```bash
curl -X GET http://localhost:8080/api/health
```

**Detailed Health:**
```bash
curl -X GET http://localhost:8080/api/health/detailed
```

## Environment Variables for Testing

```bash
# Export these after login
export RIDER_TOKEN="your-rider-jwt-token"
export DRIVER_TOKEN="your-driver-jwt-token"
export TRIP_ID="1"
```

## Common HTTP Status Codes

- `200 OK` - Success
- `201 Created` - Resource created
- `400 Bad Request` - Invalid input
- `401 Unauthorized` - Missing or invalid token
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

## Response Format

All successful responses follow this format:
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2025-12-07T10:30:00"
}
```

Error responses:
```json
{
  "timestamp": "2025-12-07T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/trips/request"
}
```
