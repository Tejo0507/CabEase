-- Flyway baseline migration for CabEase
-- Creates core tables: users, drivers, cabs, bookings, feedbacks
-- Designed for PostgreSQL (compatible with H2 for simple types)

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(150),
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_USER'
);

CREATE TABLE drivers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    license_number VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(32) NOT NULL,
    email VARCHAR(255),
    available BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE cabs (
    id BIGSERIAL PRIMARY KEY,
    cab_number VARCHAR(100) NOT NULL UNIQUE,
    model VARCHAR(255) NOT NULL,
    brand VARCHAR(255) NOT NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    capacity INTEGER NOT NULL,
    price_per_km NUMERIC(12,4) NOT NULL,
    available BOOLEAN NOT NULL DEFAULT true,
    driver_id BIGINT,
    current_latitude NUMERIC(10,8),
    current_longitude NUMERIC(11,8),
    last_location_update TIMESTAMP,
    color VARCHAR(100),
    year INTEGER,
    fuel_type VARCHAR(50),
    ac_available BOOLEAN NOT NULL DEFAULT false,
    music_system BOOLEAN NOT NULL DEFAULT false,
    gps_enabled BOOLEAN NOT NULL DEFAULT true,
    verified BOOLEAN NOT NULL DEFAULT false,
    insurance_number VARCHAR(255),
    insurance_expiry TIMESTAMP,
    registration_number VARCHAR(255),
    registration_expiry TIMESTAMP,
    average_rating DOUBLE PRECISION NOT NULL DEFAULT 0,
    total_trips INTEGER NOT NULL DEFAULT 0,
    total_earnings NUMERIC(18,4) NOT NULL DEFAULT 0,
    allow_female_driver_preference BOOLEAN NOT NULL DEFAULT false,
    surge_eligible BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_cab_driver FOREIGN KEY (driver_id) REFERENCES drivers(id) ON DELETE SET NULL
);

CREATE INDEX idx_cab_vehicle_type ON cabs (vehicle_type);
CREATE INDEX idx_cab_available ON cabs (available);
CREATE INDEX idx_cab_location ON cabs (current_latitude, current_longitude);

CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    pickup_location TEXT NOT NULL,
    drop_location TEXT NOT NULL,
    pickup_latitude NUMERIC(10,8),
    pickup_longitude NUMERIC(11,8),
    drop_latitude NUMERIC(10,8),
    drop_longitude NUMERIC(11,8),
    booking_date_time TIMESTAMP NOT NULL,
    scheduled_for TIMESTAMP,
    is_scheduled BOOLEAN NOT NULL DEFAULT false,
    distance NUMERIC(12,4) NOT NULL,
    base_fare NUMERIC(18,4) NOT NULL,
    surge_multiplier NUMERIC(8,4),
    surge_amount NUMERIC(18,4),
    tax_amount NUMERIC(18,4),
    discount_amount NUMERIC(18,4),
    total_fare NUMERIC(18,4) NOT NULL,
    preferred_vehicle_type VARCHAR(50),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    user_id BIGINT NOT NULL,
    cab_id BIGINT,
    female_driver_preferred BOOLEAN NOT NULL DEFAULT false,
    allow_sharing BOOLEAN NOT NULL DEFAULT false,
    is_emergency BOOLEAN NOT NULL DEFAULT false,
    payment_method VARCHAR(50),
    payment_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    payment_transaction_id VARCHAR(255),
    trip_started_at TIMESTAMP,
    trip_ended_at TIMESTAMP,
    driver_assigned_at TIMESTAMP,
    driver_arrived_at TIMESTAMP,
    user_rating INTEGER,
    driver_rating INTEGER,
    user_feedback TEXT,
    driver_feedback TEXT,
    sos_triggered BOOLEAN NOT NULL DEFAULT false,
    sos_triggered_at TIMESTAMP,
    trip_shared BOOLEAN NOT NULL DEFAULT false,
    cancellation_reason TEXT,
    cancelled_at TIMESTAMP,
    cancellation_fee NUMERIC(18,4),
    special_instructions TEXT,
    need_wheelchair_access BOOLEAN,
    need_child_seat BOOLEAN,
    passenger_count INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_cab FOREIGN KEY (cab_id) REFERENCES cabs(id) ON DELETE SET NULL
);

CREATE INDEX idx_booking_status ON bookings (status);
CREATE INDEX idx_booking_user ON bookings (user_id);
CREATE INDEX idx_booking_scheduled ON bookings (scheduled_for);
CREATE INDEX idx_booking_created ON bookings (created_at);

CREATE TABLE feedbacks (
    id BIGSERIAL PRIMARY KEY,
    rating INTEGER NOT NULL,
    comment TEXT,
    user_id BIGINT NOT NULL,
    booking_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    CONSTRAINT fk_feedback_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_feedback_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
);

CREATE INDEX idx_feedback_user ON feedbacks (user_id);

-- Optional: Postgres-specific improvements (not executed by default)
-- To enable geospatial indexing and efficient radius search, consider installing PostGIS
-- and replacing latitude/longitude with a geometry POINT and adding a GiST index.
