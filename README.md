# Ride Hailing Backend

## System Design
```mermaid
flowchart TD
    C[Client Apps\nRider | Driver | Admin]
    G[API Gateway Layer\nSpring Boot REST Controllers]
    S[Security Layer\nJWT Auth + RBAC]

    subgraph APP[Ride Hailing Backend - Modular Monolith]
      A1[Auth Module]
      A2[Rider Module]
      A3[Driver Module]
      A4[Trip Module\nLifecycle Orchestration]
      A5[Pricing Module\nFare + Surge]
      A6[Payment Module\nCommission + Earnings]
      A7[Rating Module]
      A8[Analytics/Admin/Health]
      EV[Event Publisher\nTrip Events]
    end

    DB[(PostgreSQL\nTransactional Data)]
    R[(Redis\nDriver Locations Cache)]
    K[(Kafka\nAsync Messaging)]

    C --> G --> S --> A4
    S --> A1
    S --> A2
    S --> A3
    A4 --> A5
    A4 --> A6
    A4 --> EV
    A3 --> R
    A1 --> DB
    A2 --> DB
    A3 --> DB
    A4 --> DB
    A5 --> DB
    A6 --> DB
    A7 --> DB
    A8 --> DB
    EV --> K
```

A production-style backend for a ride-hailing platform (Uber/Lyft-like), built with Spring Boot as a modular monolith.

## What It Does
- Handles signup/login with JWT authentication and role-based access (`RIDER`, `DRIVER`, `ADMIN`).
- Manages full trip lifecycle: request, match, accept, start, complete, cancel.
- Calculates fare estimates with surge pricing.
- Processes payment records and calculates driver earnings with commission split.
- Supports ratings, analytics, admin operations, and health checks.

## How It Works
- **API layer:** REST controllers grouped by domain (`/api/auth`, `/api/trips`, `/api/drivers`, etc.).
- **Service layer:** business orchestration in services like `TripService`, `PaymentService`, `PricingService`.
- **Persistence:** PostgreSQL via Spring Data JPA; schema versioning via Flyway migrations.
- **Realtime/scale patterns:**
  - Redis stores driver location snapshots for quick proximity checks.
  - Kafka carries trip lifecycle events for async notifications.

## Core Modules
- `auth`, `rider`, `driver`, `trip`, `pricing`, `payment`, `rating`
- `analytics`, `admin`, `health`
- `common` (security/config/exceptions/utils)

## Main Data Tables
`users`, `riders`, `drivers`, `vehicles`, `trips`, `saved_locations`, `ratings`, `payments`, `driver_earnings`

## Quick Start
### Prerequisites
- Java 17+
- Maven 3.8+
- Docker + Docker Compose

### Run
```bash
docker-compose up -d
mvn clean install
mvn spring-boot:run
```

API base URL: `http://localhost:8080`

## Notes
- Architecture details and refactor roadmap: `docs/architecture/ARCHITECTURE.md`
- Current implementation is a modular monolith (single deployable service).
