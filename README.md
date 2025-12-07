# Ride Hailing Backend - Production-Ready Platform

A **FAANG-level** ride-hailing backend built with Spring Boot 3, implementing features similar to Uber/Lyft with microservices-ready architecture.

## 🚀 Features

### Core Functionality
- ✅ **User Authentication** - JWT-based auth with role-based access control (Rider, Driver, Admin)
- ✅ **Trip Management** - Complete trip lifecycle: request → match → accept → start → complete
- ✅ **Driver Matching** - Find nearest available drivers using geospatial queries
- ✅ **Dynamic Pricing** - Surge pricing based on demand/supply ratio
- ✅ **Rating System** - Two-way ratings between riders and drivers
- ✅ **Payment Processing** - Simulated payment gateway with transaction tracking
- ✅ **Driver Earnings** - Automatic commission calculation and payout tracking
- ✅ **Real-time Notifications** - Kafka-based event streaming for trip updates
- ✅ **Analytics Dashboard** - Platform statistics and user insights
- ✅ **Admin Panel** - Platform management and monitoring

### Technical Features
- 🏗️ **Clean Architecture** - Layered design with clear separation of concerns
- 🔐 **Security** - Spring Security with JWT, BCrypt password hashing
- 📊 **Database Migrations** - Flyway for version-controlled schema management
- 🚀 **Caching** - Redis for driver location tracking
- 📨 **Event-Driven** - Kafka for async communication and scalability
- 🔍 **Monitoring** - Health checks and detailed system status endpoints
- ⚡ **Performance** - Connection pooling, indexed queries, optimized search
- 📝 **Validation** - Request validation with Jakarta Bean Validation

## 📦 Technology Stack

| Component | Technology |
|-----------|-----------|
| Framework | Spring Boot 3.2.0 |
| Language | Java 17 |
| Database | PostgreSQL 15 |
| Cache | Redis 7 |
| Message Queue | Apache Kafka 7.5.0 |
| ORM | Spring Data JPA / Hibernate |
| Migration | Flyway |
| Security | Spring Security + JWT |
| Build Tool | Maven |
| Containerization | Docker Compose |

## 🏗️ Architecture

```
src/main/java/com/ridehailing/
├── auth/              # Authentication & Authorization
├── rider/             # Rider management & saved locations
├── driver/            # Driver management & location tracking
├── trip/              # Trip lifecycle & matching
├── pricing/           # Dynamic pricing & surge calculation
├── payment/           # Payment processing & driver earnings
├── rating/            # Two-way rating system
├── notification/      # Kafka event consumers
├── analytics/         # Platform statistics
├── admin/             # Admin endpoints
├── health/            # Health checks & monitoring
└── common/            # Shared utilities, security, exceptions
```

## 🗄️ Database Schema

**9 Tables:**
1. `users` - Base authentication
2. `riders` - Rider profiles
3. `drivers` - Driver profiles with ratings
4. `vehicles` - Driver vehicle information
5. `trips` - Trip records with full lifecycle
6. `saved_locations` - Rider favorite addresses
7. `ratings` - Two-way ratings and comments
8. `payments` - Payment transactions
9. `driver_earnings` - Commission and payout tracking

## 🔌 API Endpoints

### Authentication (`/api/auth`)
```
POST /signup      - Register new user (rider/driver)
POST /login       - Authenticate and get JWT token
```

### Rider (`/api/riders`)
```
GET  /profile                - Get rider profile
POST /locations              - Save favorite location
GET  /locations              - List saved locations
DELETE /locations/{id}       - Delete saved location
```

### Driver (`/api/drivers`)
```
GET  /profile                - Get driver profile
POST /vehicle                - Register vehicle
POST /status/online          - Go online
POST /status/offline         - Go offline
POST /location               - Update current location
```

### Trips (`/api/trips`)
```
POST /request                - Request a trip (rider)
POST /{id}/accept            - Accept trip (driver)
POST /{id}/start             - Start trip (driver)
POST /{id}/complete          - Complete trip (driver)
POST /{id}/cancel            - Cancel trip (rider/driver)
GET  /{id}                   - Get trip details
GET  /rider/history          - Rider trip history
GET  /driver/history         - Driver trip history
GET  /rider/active           - Current active trip
GET  /driver/active          - Driver's active trip
```

### Ratings (`/api/ratings`)
```
POST /driver                 - Rate driver (rider)
POST /rider                  - Rate rider (driver)
GET  /trip/{id}              - Get ratings for trip
GET  /driver/received        - Driver's received ratings
```

### Pricing (`/api/pricing`)
```
POST /estimate               - Get price estimate
```

### Analytics (`/api/analytics`)
```
GET /platform                - Platform statistics (admin)
GET /driver                  - Driver statistics
GET /rider                   - Rider statistics
```

### Admin (`/api/admin`)
```
GET /stats                   - Platform stats
GET /drivers                 - List all drivers
GET /riders                  - List all riders
GET /trips                   - List all trips
GET /drivers/{id}            - Get driver details
GET /riders/{id}             - Get rider details
GET /trips/{id}              - Get trip details
```

### Health (`/api/health`)
```
GET /                        - Basic health check
GET /detailed                - Detailed component status
GET /ready                   - Readiness probe
GET /live                    - Liveness probe
```

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Docker & Docker Compose
- Maven 3.8+

### Quick Start

1. **Clone the repository**
```bash
git clone <repository-url>
cd java-ride-booking
```

2. **Start infrastructure**
```bash
docker-compose up -d
```
This starts PostgreSQL, Redis, Zookeeper, and Kafka.

3. **Build the application**
```bash
mvn clean install
```

4. **Run the application**
```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

### Alternative: Run everything in Docker
```bash
# Build JAR
mvn clean package

# Add to docker-compose.yml and run
docker-compose up -d
```

## 🔧 Configuration

Key configuration in `application.yml`:

```yaml
app:
  jwt:
    expiration: 86400000  # 24 hours
  pricing:
    base-fare: 2.50
    per-km-rate: 1.20
    surge.max-multiplier: 3.0
  driver:
    matching:
      search-radius-km: 5.0
  payment:
    commission-rate: 20.0
```

## 📊 Example Workflow

### 1. Register Users
```bash
# Register Rider
POST /api/auth/signup
{
  "email": "rider@example.com",
  "password": "password123",
  "name": "John Doe",
  "phoneNumber": "+1234567890",
  "role": "RIDER"
}

# Register Driver
POST /api/auth/signup
{
  "email": "driver@example.com",
  "password": "password123",
  "name": "Jane Smith",
  "phoneNumber": "+0987654321",
  "role": "DRIVER"
}
```

### 2. Driver Setup
```bash
# Register vehicle
POST /api/drivers/vehicle
Authorization: Bearer <driver-token>
{
  "plateNumber": "ABC123",
  "model": "Toyota Camry",
  "color": "Black",
  "year": 2022
}

# Go online
POST /api/drivers/status/online
Authorization: Bearer <driver-token>

# Update location
POST /api/drivers/location
Authorization: Bearer <driver-token>
{
  "latitude": 37.7749,
  "longitude": -122.4194
}
```

### 3. Request Trip
```bash
POST /api/trips/request
Authorization: Bearer <rider-token>
{
  "originLatitude": 37.7749,
  "originLongitude": -122.4194,
  "destinationLatitude": 37.7849,
  "destinationLongitude": -122.4094
}
```

### 4. Complete Trip Lifecycle
```bash
# Driver accepts
POST /api/trips/{tripId}/accept
Authorization: Bearer <driver-token>

# Driver starts trip
POST /api/trips/{tripId}/start
Authorization: Bearer <driver-token>

# Driver completes trip
POST /api/trips/{tripId}/complete
Authorization: Bearer <driver-token>
```

### 5. Rate Each Other
```bash
# Rider rates driver
POST /api/ratings/driver
Authorization: Bearer <rider-token>
{
  "tripId": 1,
  "rating": 5.0,
  "comment": "Great driver!"
}

# Driver rates rider
POST /api/ratings/rider
Authorization: Bearer <driver-token>
{
  "tripId": 1,
  "rating": 5.0,
  "comment": "Friendly passenger"
}
```

## 🎯 Key Algorithms

### Surge Pricing
```
surge = 1.0 + (activeRequests / onlineDrivers) * 0.5
surge = min(surge, maxSurgeMultiplier)
finalPrice = basePrice * surge
```

### Driver Matching
- Uses Haversine formula for distance calculation
- Searches within configurable radius (default 5km)
- Returns nearest available driver
- Production: Would use Redis GEORADIUS for O(log N) performance

### Commission Calculation
```
commissionAmount = fareAmount * (commissionRate / 100)
driverEarnings = fareAmount - commissionAmount
```

## 🔐 Security

- **JWT Authentication** with configurable expiration
- **Role-Based Access Control** (RBAC)
- **BCrypt Password Hashing** (strength: 10)
- **CORS Configuration** (customize for production)
- **Input Validation** on all endpoints
- **SQL Injection Prevention** via JPA/Hibernate

## 📈 Scalability Considerations

### Current Implementation
- Single instance deployment
- PostgreSQL for persistence
- Redis for caching
- Kafka for async processing

### Production Scaling Path
1. **Horizontal Scaling**: Run multiple instances behind load balancer
2. **Database**: Read replicas, connection pooling
3. **Redis**: Redis Cluster with GEORADIUS for spatial queries
4. **Kafka**: Multi-broker setup with partitioning
5. **Caching**: Add CDN for static content, API gateway caching
6. **Monitoring**: Prometheus + Grafana, distributed tracing with Jaeger
7. **Service Mesh**: Consider Istio for service-to-service communication

## 🧪 Testing

```bash
# Run tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## 📝 Future Enhancements

- [ ] WebSocket support for real-time updates
- [ ] Route optimization with Google Maps API
- [ ] Scheduled rides
- [ ] Ride sharing (carpooling)
- [ ] Driver heat maps
- [ ] Promotional codes & discounts
- [ ] Integration tests with Testcontainers
- [ ] GraphQL API
- [ ] OpenAPI/Swagger documentation
- [ ] Internationalization (i18n)
- [ ] Multi-tenant support

## 🤝 Contributing

Contributions are welcome! Please follow these steps:
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👨‍💻 Author

Built with ❤️ as a demonstration of production-ready Spring Boot architecture.

---

**Note**: This is a demonstration project. For production use:
- Replace mock payment gateway with real integration (Stripe, PayPal)
- Implement actual push notifications (FCM, APNs)
- Add comprehensive monitoring and alerting
- Set up CI/CD pipelines
- Implement rate limiting
- Add API versioning
- Set up proper secrets management
- Implement data encryption at rest
- Add audit logging
