# Ride Hailing Backend Architecture

## 1. Executive Summary
This project is a **modular monolith** built on Spring Boot. Domain modules are separated by package (`auth`, `rider`, `driver`, `trip`, `pricing`, `payment`, `rating`, etc.), but some module boundaries are currently soft (direct cross-module repository/service usage and DTO coupling).

This document formalizes:
- Current architecture (as-is)
- Target architecture (to-be)
- Explicit dependency rules
- Priority refactor roadmap

## 2. System Style
- Architecture style: Modular Monolith
- Runtime: Single deployable Spring Boot service
- Data: PostgreSQL (system of record), Redis (location/cache), Kafka (async events)
- Integration style: Synchronous service calls + asynchronous domain events

## 3. Logical Layers
Every business module should follow this layering:
1. `controller` (API adapter)
2. `service` (application/use-case orchestration)
3. `domain` (entities, enums, value behavior)
4. `repository` (persistence adapter)
5. `dto` (API contracts)

Shared technical components live in `common`.

## 4. Current Module Map
- `auth`: identity + JWT issuance
- `rider`: rider profile + saved locations
- `driver`: driver profile, status, location
- `trip`: trip lifecycle and orchestration
- `pricing`: fare estimation and surge logic
- `payment`: payment + driver earnings
- `rating`: two-way feedback
- `notification`: Kafka consumers and message DTOs
- `analytics`: aggregate reporting
- `admin`: privileged read endpoints
- `health`: operational probes
- `common`: cross-cutting config/security/exceptions/utils

## 5. Primary Runtime Flows
- **Trip request flow**: Rider -> TripService -> PricingService + DriverMatchingService -> TripRepository -> Event Publisher (Kafka)
- **Trip completion flow**: Driver -> TripService -> TripRepository + DriverRepository -> event publication
- **Admin reporting flow**: Admin endpoints -> analytics + cross-domain query services

## 6. Architecture Findings (As-Is)
1. **Controller-to-repository leakage exists**
   - Example before fix: `admin/controller/AdminController` read repositories directly.
2. **Cross-module coupling is high in orchestration services**
   - `TripService`, `RatingService`, `AnalyticsService` access several external module repositories/services directly.
3. **Event contract ownership is inverted in one place**
   - `trip` depends on `notification.dto.TripEventDto`; event contracts should be owned by publisher domain or a neutral contracts package.
4. **Domain entities are exposed from admin endpoints**
   - Direct entity exposure increases accidental API break risk.
5. **Architecture rules are implicit, not enforceable**
   - No automated architecture tests (e.g., ArchUnit).

## 7. Applied Fix (This Change)
- Introduced `admin/service/AdminQueryService`.
- `AdminController` should now delegate all reads through the service layer, restoring controller thinness and layer discipline.

## 8. Target Architecture (To-Be)
- Keep **modular monolith** shape, but enforce strict dependency boundaries:
  - Controllers depend only on module application services and API DTOs.
  - Services may orchestrate other module services through explicit facades/ports (not direct repository reach-in across modules).
  - Repositories are module-internal adapters.
  - Event schemas move to `trip.event.contract` (or `common.event`) to avoid reverse ownership.
  - External API uses response DTOs; do not expose JPA entities.

## 9. Dependency Rules (Formal)
1. `controller -> service -> repository` only (no controller -> repository).
2. Cross-module access should prefer `service`/facade boundaries, not external repositories.
3. `domain` should not depend on `controller` or `config`.
4. Kafka DTOs should be owned by event publisher domain or a shared contract package.
5. `common` must remain technical/cross-cutting (no business logic drift).

## 10. Refactor Roadmap (Prioritized)
### P0
- Complete admin boundary cleanup (controller delegates only).
- Add API DTOs for admin responses (avoid entity exposure).

### P1
- Introduce module facades for `driver`, `rider`, `trip` queries used cross-module.
- Remove direct cross-module repository usage from orchestration services where possible.

### P2
- Move `TripEventDto` to domain-owned contract package.
- Add architecture tests (ArchUnit) to enforce rules automatically.

### P3
- Optional package evolution toward hexagonal style per module:
  - `application`, `domain`, `infrastructure`, `api`

## 11. Governance
- Any new endpoint must keep controllers thin and return DTOs.
- Any cross-module dependency should document why local ownership is insufficient.
- CI should fail when architecture rules are violated (after ArchUnit adoption).
