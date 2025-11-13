# 🎯 CabEase Production Database - Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         CabEase Application                              │
│                         Spring Boot 2.7.17                               │
│                         Port: 8080                                       │
└────────────┬────────────────────────────────────────────┬────────────────┘
             │                                            │
             │ Spring Data JPA / Hibernate                │ Spring Actuator
             │ HikariCP Connection Pool                   │ Health Checks
             │                                            │
             ▼                                            ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                     PostgreSQL 15 Database                               │
│                     Host: localhost | Port: 5432                         │
│                     Database: cabease                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────┐      │
│  │                      CORE TABLES                              │      │
│  ├──────────────────────────────────────────────────────────────┤      │
│  │                                                               │      │
│  │  ┌────────────┐    ┌──────────┐    ┌────────────────────┐   │      │
│  │  │   users    │    │ drivers  │    │       cabs         │   │      │
│  │  ├────────────┤    ├──────────┤    ├────────────────────┤   │      │
│  │  │ id (PK)    │    │ id (PK)  │    │ id (PK)            │   │      │
│  │  │ username   │    │ name     │    │ cab_number         │   │      │
│  │  │ email      │    │ license  │    │ model, brand       │   │      │
│  │  │ password   │    │ phone    │    │ vehicle_type       │   │      │
│  │  │ role       │    │ email    │◄───┤ driver_id (FK)     │   │      │
│  │  └────────────┘    │ available│    │ current_lat/long   │   │      │
│  │        │            └──────────┘    │ price_per_km       │   │      │
│  │        │                            │ ac_available       │   │      │
│  │        │                            │ average_rating     │   │      │
│  │        │                            │ total_trips        │   │      │
│  │        │                            │ total_earnings     │   │      │
│  │        │                            │ ... (28 fields)    │   │      │
│  │        │                            └────────────────────┘   │      │
│  │        │                                      │              │      │
│  │        │                                      │              │      │
│  │        ▼                                      ▼              │      │
│  │  ┌─────────────────────────────────────────────────────┐    │      │
│  │  │              bookings                                │    │      │
│  │  ├─────────────────────────────────────────────────────┤    │      │
│  │  │ id (PK)                                              │    │      │
│  │  │ user_id (FK) ────────────────────┘                  │    │      │
│  │  │ cab_id (FK)  ────────────────────────────┘          │    │      │
│  │  │ pickup_location, drop_location                      │    │      │
│  │  │ pickup_lat/long, drop_lat/long                      │    │      │
│  │  │ booking_date_time, scheduled_for                    │    │      │
│  │  │ distance, base_fare, total_fare                     │    │      │
│  │  │ surge_multiplier, tax_amount, discount              │    │      │
│  │  │ status, payment_method, payment_status              │    │      │
│  │  │ trip_started_at, trip_ended_at                      │    │      │
│  │  │ user_rating, driver_rating                          │    │      │
│  │  │ sos_triggered, cancellation_reason                  │    │      │
│  │  │ ... (50+ fields)                                    │    │      │
│  │  └─────────────────────────────────────────────────────┘    │      │
│  │        │                                                     │      │
│  │        │                                                     │      │
│  │        ▼                                                     │      │
│  │  ┌──────────────────────────────┐                           │      │
│  │  │        feedbacks              │                           │      │
│  │  ├──────────────────────────────┤                           │      │
│  │  │ id (PK)                       │                           │      │
│  │  │ user_id (FK) ─────┘           │                           │      │
│  │  │ booking_id (FK) ──────────────┘                           │      │
│  │  │ rating (1-5)                  │                           │      │
│  │  │ comment                       │                           │      │
│  │  └──────────────────────────────┘                           │      │
│  │                                                               │      │
│  └──────────────────────────────────────────────────────────────┘      │
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────┐      │
│  │              PERFORMANCE INDEXES (25+)                        │      │
│  ├──────────────────────────────────────────────────────────────┤      │
│  │ • idx_cab_vehicle_type         • idx_booking_status           │      │
│  │ • idx_cab_available            • idx_booking_user             │      │
│  │ • idx_cab_location             • idx_booking_scheduled        │      │
│  │ • idx_cab_driver_id            • idx_booking_user_status      │      │
│  │ • idx_cab_verified             • idx_cab_type_available       │      │
│  │ • idx_driver_available         • idx_user_email               │      │
│  │ • ... and 15+ more composite indexes                          │      │
│  └──────────────────────────────────────────────────────────────┘      │
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────┐      │
│  │                   VIEWS & ANALYTICS                           │      │
│  ├──────────────────────────────────────────────────────────────┤      │
│  │                                                               │      │
│  │  v_available_cabs                                             │      │
│  │  ├─ Real-time view of available cabs with driver info        │      │
│  │  └─ SELECT * FROM v_available_cabs WHERE ...                 │      │
│  │                                                               │      │
│  │  v_booking_stats                                              │      │
│  │  ├─ Daily booking statistics and revenue                     │      │
│  │  └─ SELECT * FROM v_booking_stats WHERE ...                  │      │
│  │                                                               │      │
│  │  mv_driver_performance (Materialized)                         │      │
│  │  ├─ Driver performance metrics (refresh periodically)         │      │
│  │  └─ REFRESH MATERIALIZED VIEW mv_driver_performance           │      │
│  │                                                               │      │
│  └──────────────────────────────────────────────────────────────┘      │
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────┐      │
│  │                 TRIGGERS & AUTOMATION                         │      │
│  ├──────────────────────────────────────────────────────────────┤      │
│  │ • update_users_updated_at     (Auto-update timestamps)        │      │
│  │ • update_drivers_updated_at   (Auto-update timestamps)        │      │
│  │ • update_cabs_updated_at      (Auto-update timestamps)        │      │
│  │ • update_bookings_updated_at  (Auto-update timestamps)        │      │
│  └──────────────────────────────────────────────────────────────┘      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
             │                                            │
             │                                            │
             ▼                                            ▼
┌────────────────────────┐                   ┌─────────────────────────┐
│      pgAdmin 4         │                   │   Flyway Migrations     │
│   Port: 8081           │                   │   V1__create_schema     │
│   Visual DB Admin      │                   │   V2__optimizations     │
└────────────────────────┘                   │   V3__seed_data         │
                                             └─────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────┐
│                         DATA INTEGRITY                                   │
├─────────────────────────────────────────────────────────────────────────┤
│ ✓ Foreign Keys: 4 (maintain referential integrity)                      │
│ ✓ Check Constraints: 8+ (rating range, positive values)                 │
│ ✓ Unique Constraints: 5+ (email, license, cab_number)                   │
│ ✓ Not Null: 50+ (critical fields)                                       │
└─────────────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────┐
│                    CONNECTION FLOW                                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  Application Startup                                                     │
│       │                                                                  │
│       ├─► 1. Read application-prod.properties                           │
│       │       SPRING_DATASOURCE_URL (from environment)                   │
│       │       SPRING_DATASOURCE_USERNAME (from environment)              │
│       │       SPRING_DATASOURCE_PASSWORD (from environment)              │
│       │                                                                  │
│       ├─► 2. HikariCP creates connection pool                           │
│       │       • maximum-pool-size: 20                                    │
│       │       • minimum-idle: 5                                          │
│       │       • connection-timeout: 30s                                  │
│       │                                                                  │
│       ├─► 3. Flyway checks for pending migrations                       │
│       │       • V1__create_schema.sql (if not run)                       │
│       │       • V2__add_production_optimizations.sql (if not run)        │
│       │       • V3__seed_sample_data.sql (if not run)                    │
│       │                                                                  │
│       ├─► 4. Hibernate initializes JPA entity manager                   │
│       │       • Maps entities to tables                                  │
│       │       • Validates schema                                         │
│       │                                                                  │
│       └─► 5. Application ready to accept requests                       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────┐
│                      SAMPLE DATA LOADED                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  Users: 5                                                                │
│  ├─ admin@cabease.com (ROLE_ADMIN)                                      │
│  ├─ john.doe@example.com (ROLE_USER)                                    │
│  ├─ jane.smith@example.com (ROLE_USER)                                  │
│  ├─ bob.wilson@example.com (ROLE_USER)                                  │
│  └─ alice.johnson@example.com (ROLE_USER)                               │
│                                                                          │
│  Drivers: 10                                                             │
│  ├─ Rajesh Kumar (DL1420110012345) ✓ Available                          │
│  ├─ Priya Singh (DL1420110012346) ✓ Available                           │
│  ├─ Mohammed Aslam (DL1420110012347) ✓ Available                        │
│  └─ ... 7 more drivers                                                  │
│                                                                          │
│  Cabs: 12                                                                │
│  ├─ KA01AB1234 - Swift Dzire (SEDAN) - ₹12.50/km                        │
│  ├─ KA04IJ7890 - Innova Crysta (SUV) - ₹18.00/km                        │
│  ├─ KA09UV2345 - E-Class (LUXURY) - ₹35.00/km                           │
│  └─ ... 9 more vehicles                                                 │
│                                                                          │
│  Bookings: 8                                                             │
│  ├─ 5 Completed trips with ratings                                      │
│  ├─ 1 In-progress trip                                                  │
│  ├─ 1 Pending booking                                                   │
│  └─ 1 Scheduled future trip                                             │
│                                                                          │
│  Feedbacks: 5                                                            │
│  └─ All completed trips have user feedback                              │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────┐
│                      DEPLOYMENT OPTIONS                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  Local Development (Docker)                                              │
│  └─► docker-compose up -d                                               │
│                                                                          │
│  Local Development (Manual PostgreSQL)                                   │
│  └─► Install PostgreSQL 15 → Create 'cabease' database                  │
│                                                                          │
│  Cloud Deployment                                                        │
│  ├─► AWS RDS PostgreSQL                                                 │
│  ├─► Azure Database for PostgreSQL                                      │
│  ├─► Google Cloud SQL for PostgreSQL                                    │
│  ├─► Heroku Postgres                                                    │
│  └─► Supabase (Free tier available)                                     │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────┐
│                       QUICK START COMMAND                                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   .\start-cabease-production.ps1 -UseDocker                             │
│                                                                          │
│   This single command will:                                              │
│   ✓ Check prerequisites (Java, Maven, Docker)                           │
│   ✓ Start PostgreSQL + pgAdmin in Docker                                │
│   ✓ Build the CabEase application                                       │
│   ✓ Set environment variables                                           │
│   ✓ Run Flyway migrations (create schema + seed data)                   │
│   ✓ Launch application on http://localhost:8080                         │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

## Architecture Principles

### 1. **Separation of Concerns**
- Application layer (Spring Boot)
- Data access layer (Spring Data JPA)
- Database layer (PostgreSQL)

### 2. **Scalability**
- Connection pooling (HikariCP)
- Indexed queries
- Materialized views for analytics
- Stateless application design

### 3. **Data Integrity**
- Foreign key constraints
- Check constraints
- Unique constraints
- Not null constraints

### 4. **Performance**
- 25+ strategic indexes
- Composite indexes for common queries
- Materialized views
- Efficient query plans

### 5. **Maintainability**
- Flyway migrations (versioned schema)
- Clear naming conventions
- Comprehensive documentation
- Automated deployment scripts

### 6. **Security**
- BCrypt password hashing
- Environment-based configuration
- Role-based access control
- SQL injection prevention (JPA)

### 7. **Observability**
- Actuator health checks
- Database views for analytics
- Audit timestamps
- pgAdmin for monitoring

---

## Technology Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Application** | Spring Boot 2.7.17 | Web framework |
| **ORM** | Hibernate / JPA | Object-relational mapping |
| **Connection Pool** | HikariCP | Database connection management |
| **Database** | PostgreSQL 15 | Primary data store |
| **Migration** | Flyway | Schema version control |
| **Monitoring** | Spring Actuator | Health checks & metrics |
| **Admin UI** | pgAdmin 4 | Database management |
| **Container** | Docker Compose | Local deployment |
| **Build** | Maven | Dependency & build management |

---

**Your CabEase database architecture is production-ready! 🚀**
