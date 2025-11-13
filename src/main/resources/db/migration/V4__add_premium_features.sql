-- V4: Add Premium Features to CabEase
-- This migration adds tables for enhanced features: scheduling, payments, ratings, promos, notifications, SOS, profiles

-- ============================================
-- RIDE SCHEDULING
-- ============================================
CREATE TABLE ride_schedules (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    pickup_location VARCHAR(500) NOT NULL,
    pickup_latitude NUMERIC(10, 8),
    pickup_longitude NUMERIC(11, 8),
    drop_location VARCHAR(500) NOT NULL,
    drop_latitude NUMERIC(10, 8),
    drop_longitude NUMERIC(11, 8),
    scheduled_time TIMESTAMP NOT NULL,
    vehicle_type VARCHAR(50) EFAULT 1,
    special_instructions TEXT,
    status VARCHAR(50) DEFAULT 'SCHEDULED', -- SCHEDULED, CONFIRMED, CANCELLED, COMPLETED
    estimated_fare NUMERIC(19, 2),
    booking_id BIGINT REFERENCES bookings(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cancelled_at TIMESTAMP,
    cancellation_reason TEXT
);

CREATE INDEX idx_schedule_user ON ride_schedules(user_id);
CREATE INDEX idx_schedule_time ON ride_schedules(scheduled_time);
CREATE INDEX idx_schedule_status ON ride_schedules(status);

-- ============================================
-- PAYMENT METHODS
-- ============================================
CREATE TABLE payment_methods (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    method_type VARCHAR(50) NOT NULL, -- WALLET, UPI, CARD, CASH
    card_last_four VARCHAR(4),
    card_brand VARCHAR(50),
    upi_id VARCHAR(255),
    wallet_balance NUMERIC(19, 2) DEFAULT 0.00,
    is_default BOOLEAN DEFAULT false,
    is_active BOOLEAN DEFAULT true,
    expiry_month INTEGER,
    expiry_year INTEGER,
    cardholder_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
NOT NULL,
    passenger_count INTEGER D
CREATE INDEX idx_payment_user ON payment_methods(user_id);
CREATE INDEX idx_payment_default ON payment_methods(is_default);

-- ============================================
-- WALLET TRANSACTIONS
-- ============================================
CREATE TABLE wallet_transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    transaction_type VARCHAR(50) NOT NULL, -- CREDIT, DEBIT, REFUND
    amount NUMERIC(19, 2) NOT NULL,
    balance_before NUMERIC(19, 2) NOT NULL,
    balance_after NUMERIC(19, 2) NOT NULL,
    booking_id BIGINT REFERENCES bookings(id) ON DELETE SET NULL,
    description TEXT,
    transaction_id VARCHAR(255) UNIQUE,
    status VARCHAR(50) DEFAULT 'COMPLETED', -- PENDING, COMPLETED, FAILED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_wallet_user ON wallet_transactions(user_id);
CREATE INDEX idx_wallet_transaction ON wallet_transactions(transaction_id);

-- ============================================
-- DRIVER PROFILES (Enhanced)
-- ============================================
CREATE TABLE driver_profiles (
    id BIGSERIAL PRIMARY KEY,
    driver_id BIGINT NOT NULL UNIQUE REFERENCES drivers(id) ON DELETE CASCADE,
    photo_url VARCHAR(500),
    date_of_birth DATE,
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    zip_code VARCHAR(20),
    emergency_contact_name VARCHAR(255),
    emergency_contact_phone VARCHAR(20),
    license_expiry DATE,
    total_rides INTEGER DEFAULT 0,
    total_earnings NUMERIC(19, 2) DEFAULT 0.00,
    average_rating NUMERIC(3, 2) DEFAULT 0.00,
    rating_count INTEGER DEFAULT 0,
    verification_status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, VERIFIED, REJECTED
    verified_at TIMESTAMP,
    documents_verified BOOLEAN DEFAULT false,
    background_check_status VARCHAR(50),
    joined_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_active TIMESTAMP,
    is_online BOOLEAN DEFAULT false,
    acceptance_rate NUMERIC(5, 2) DEFAULT 0.00,
    cancellation_rate NUMERIC(5, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_driver_profile_verified ON driver_profiles(verification_status);
CREATE INDEX idx_driver_profile_online ON driver_profiles(is_online);

-- ============================================
-- RATINGS & FEEDBACK (Enhanced)
-- ============================================
ALTER TABLE feedbacks ADD COLUMN IF NOT EXISTS driver_id BIGINT REFERENCES drivers(id) ON DELETE CASCADE;
ALTER TABLE feedbacks ADD COLUMN IF NOT EXISTS ride_quality INTEGER CHECK (ride_quality >= 1 AND ride_quality <= 5);
ALTER TABLE feedbacks ADD COLUMN IF NOT EXISTS vehicle_cleanliness INTEGER CHECK (vehicle_cleanliness >= 1 AND vehicle_cleanliness <= 5);
ALTER TABLE feedbacks ADD COLUMN IF NOT EXISTS driver_behavior INTEGER CHECK (driver_behavior >= 1 AND driver_behavior <= 5);
ALTER TABLE feedbacks ADD COLUMN IF NOT EXISTS punctuality INTEGER CHECK (punctuality >= 1 AND punctuality <= 5);
ALTER TABLE feedbacks ADD COLUMN IF NOT EXISTS tags TEXT[]; -- array of tags: 'Clean Vehicle', 'Polite', 'Safe Driving', etc.
ALTER TABLE feedbacks ADD COLUMN IF NOT EXISTS driver_response TEXT;
ALTER TABLE feedbacks ADD COLUMN IF NOT EXISTS is_visible BOOLEAN DEFAULT true;
ALTER TABLE feedbacks ADD COLUMN IF NOT EXISTS helpful_count INTEGER DEFAULT 0;

CREATE INDEX idx_feedback_driver ON feedbacks(driver_id);

-- ============================================
-- PROMO CODES & OFFERS
-- ============================================
CREATE TABLE promo_codes (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    discount_type VARCHAR(20) NOT NULL, -- PERCENTAGE, FIXED_AMOUNT
    discount_value NUMERIC(19, 2) NOT NULL,
    max_discount_amount NUMERIC(19, 2),
    min_ride_amount NUMERIC(19, 2),
    valid_from TIMESTAMP NOT NULL,
    valid_until TIMESTAMP NOT NULL,
    usage_limit INTEGER,
    usage_count INTEGER DEFAULT 0,
    per_user_limit INTEGER DEFAULT 1,
    applicable_vehicle_types TEXT[], -- array of vehicle types
    is_active BOOLEAN DEFAULT true,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_promo_code ON promo_codes(code);
CREATE INDEX idx_promo_active ON promo_codes(is_active);
CREATE INDEX idx_promo_validity ON promo_codes(valid_from, valid_until);

-- ============================================
-- PROMO CODE USAGE
-- ============================================
CREATE TABLE promo_code_usage (
    id BIGSERIAL PRIMARY KEY,
    promo_code_id BIGINT NOT NULL REFERENCES promo_codes(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    booking_id BIGINT REFERENCES bookings(id) ON DELETE SET NULL,
    discount_amount NUMERIC(19, 2) NOT NULL,
    used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_promo_usage_user ON promo_code_usage(user_id);
CREATE INDEX idx_promo_usage_code ON promo_code_usage(promo_code_id);

-- ============================================
-- NOTIFICATIONS
-- ============================================
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL, -- BOOKING, PAYMENT, PROMO, ALERT, SYSTEM
    related_booking_id BIGINT REFERENCES bookings(id) ON DELETE SET NULL,
    is_read BOOLEAN DEFAULT false,
    priority VARCHAR(20) DEFAULT 'NORMAL', -- LOW, NORMAL, HIGH, URGENT
    action_url VARCHAR(500),
    icon VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP
);

CREATE INDEX idx_notification_user ON notifications(user_id);
CREATE INDEX idx_notification_read ON notifications(is_read);
CREATE INDEX idx_notification_created ON notifications(created_at DESC);

-- ============================================
-- SOS ALERTS / EMERGENCY
-- ============================================
CREATE TABLE sos_alerts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    booking_id BIGINT REFERENCES bookings(id) ON DELETE SET NULL,
    latitude NUMERIC(10, 8) NOT NULL,
    longitude NUMERIC(11, 8) NOT NULL,
    location_address TEXT,
    alert_type VARCHAR(50) DEFAULT 'GENERAL', -- GENERAL, ACCIDENT, HARASSMENT, MEDICAL
    message TEXT,
    status VARCHAR(50) DEFAULT 'ACTIVE', -- ACTIVE, RESOLVED, CANCELLED
    emergency_contacts_notified BOOLEAN DEFAULT false,
    police_notified BOOLEAN DEFAULT false,
    resolution_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP,
    resolved_by BIGINT REFERENCES users(id)
);

CREATE INDEX idx_sos_user ON sos_alerts(user_id);
CREATE INDEX idx_sos_status ON sos_alerts(status);
CREATE INDEX idx_sos_created ON sos_alerts(created_at DESC);

-- ============================================
-- USER PROFILES (Enhanced)
-- ============================================
CREATE TABLE user_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    photo_url VARCHAR(500),
    phone_number VARCHAR(20),
    alternate_phone VARCHAR(20),
    date_of_birth DATE,
    gender VARCHAR(20),
    address_line1 TEXT,
    address_line2 TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    zip_code VARCHAR(20),
    country VARCHAR(100) DEFAULT 'India',
    emergency_contact_name VARCHAR(255),
    emergency_contact_phone VARCHAR(20),
    emergency_contact_relation VARCHAR(50),
    preferred_language VARCHAR(50) DEFAULT 'en',
    total_bookings INTEGER DEFAULT 0,
    total_spent NUMERIC(19, 2) DEFAULT 0.00,
    loyalty_points INTEGER DEFAULT 0,
    membership_tier VARCHAR(50) DEFAULT 'SILVER', -- SILVER, GOLD, PLATINUM, DIAMOND
    theme_preference VARCHAR(20) DEFAULT 'light', -- light, dark, auto
    notification_preferences JSONB DEFAULT '{"email": true, "sms": true, "push": true}'::jsonb,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_profile_user ON user_profiles(user_id);
CREATE INDEX idx_user_profile_tier ON user_profiles(membership_tier);

-- ============================================
-- SAVED ADDRESSES
-- ============================================
CREATE TABLE saved_addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    label VARCHAR(50) NOT NULL, -- HOME, WORK, OTHER
    address TEXT NOT NULL,
    latitude NUMERIC(10, 8),
    longitude NUMERIC(11, 8),
    landmark VARCHAR(255),
    is_default BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_saved_address_user ON saved_addresses(user_id);

-- ============================================
-- SUPPORT TICKETS
-- ============================================
CREATE TABLE support_tickets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    booking_id BIGINT REFERENCES bookings(id) ON DELETE SET NULL,
    ticket_number VARCHAR(50) UNIQUE NOT NULL,
    subject VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL, -- BOOKING, PAYMENT, DRIVER, TECHNICAL, OTHER
    priority VARCHAR(20) DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH, URGENT
    status VARCHAR(50) DEFAULT 'OPEN', -- OPEN, IN_PROGRESS, RESOLVED, CLOSED
    description TEXT NOT NULL,
    resolution TEXT,
    assigned_to BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP,
    closed_at TIMESTAMP
);

CREATE INDEX idx_ticket_user ON support_tickets(user_id);
CREATE INDEX idx_ticket_status ON support_tickets(status);
CREATE INDEX idx_ticket_number ON support_tickets(ticket_number);

-- ============================================
-- SUPPORT MESSAGES
-- ============================================
CREATE TABLE support_messages (
    id BIGSERIAL PRIMARY KEY,
    ticket_id BIGINT NOT NULL REFERENCES support_tickets(id) ON DELETE CASCADE,
    sender_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    message TEXT NOT NULL,
    attachment_url VARCHAR(500),
    is_internal BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_support_msg_ticket ON support_messages(ticket_id);

-- ============================================
-- RIDE TRACKING (Real-time locations)
-- ============================================
CREATE TABLE ride_tracking (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    driver_id BIGINT NOT NULL REFERENCES drivers(id) ON DELETE CASCADE,
    latitude NUMERIC(10, 8) NOT NULL,
    longitude NUMERIC(11, 8) NOT NULL,
    speed NUMERIC(5, 2), -- km/h
    heading NUMERIC(5, 2), -- degrees
    accuracy NUMERIC(10, 2), -- meters
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_tracking_booking ON ride_tracking(booking_id);
CREATE INDEX idx_tracking_time ON ride_tracking(timestamp DESC);

-- ============================================
-- FARE RULES (For dynamic pricing)
-- ============================================
CREATE TABLE fare_rules (
    id BIGSERIAL PRIMARY KEY,
    vehicle_type VARCHAR(50) NOT NULL,
    base_fare NUMERIC(19, 2) NOT NULL,
    per_km_rate NUMERIC(19, 2) NOT NULL,
    per_minute_rate NUMERIC(19, 2),
    minimum_fare NUMERIC(19, 2) NOT NULL,
    booking_fee NUMERIC(19, 2) DEFAULT 0.00,
    surge_multiplier NUMERIC(5, 2) DEFAULT 1.0,
    night_charge_start TIME,
    night_charge_end TIME,
    night_charge_percentage NUMERIC(5, 2),
    waiting_charge_per_minute NUMERIC(19, 2),
    free_waiting_minutes INTEGER DEFAULT 5,
    cancellation_fee NUMERIC(19, 2) DEFAULT 50.00,
    city VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    effective_from TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    effective_until TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_fare_vehicle ON fare_rules(vehicle_type);
CREATE INDEX idx_fare_active ON fare_rules(is_active);

-- ============================================
-- Insert default fare rules
-- ============================================
INSERT INTO fare_rules (vehicle_type, base_fare, per_km_rate, minimum_fare, booking_fee, city) VALUES
('SEDAN', 50.00, 12.00, 80.00, 20.00, 'Chennai'),
('SUV', 75.00, 18.00, 120.00, 20.00, 'Chennai'),
('HATCHBACK', 40.00, 10.00, 60.00, 15.00, 'Chennai'),
('LUXURY', 150.00, 25.00, 200.00, 50.00, 'Chennai');

-- ============================================
-- Insert sample promo codes
-- ============================================
INSERT INTO promo_codes (code, description, discount_type, discount_value, max_discount_amount, min_ride_amount, valid_from, valid_until, usage_limit, per_user_limit, is_active) VALUES
('FIRST50', 'Get 50% off on your first ride', 'PERCENTAGE', 50.00, 100.00, 100.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', 1000, 1, true),
('SAVE100', 'Flat ₹100 off on rides above ₹300', 'FIXED_AMOUNT', 100.00, 100.00, 300.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', 500, 3, true),
('WEEKEND20', '20% off on weekend rides', 'PERCENTAGE', 20.00, 150.00, 150.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '90 days', NULL, NULL, true);

-- ============================================
-- Triggers for updated_at timestamps
-- ============================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_ride_schedules_updated_at BEFORE UPDATE ON ride_schedules FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_payment_methods_updated_at BEFORE UPDATE ON payment_methods FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_driver_profiles_updated_at BEFORE UPDATE ON driver_profiles FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_promo_codes_updated_at BEFORE UPDATE ON promo_codes FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_user_profiles_updated_at BEFORE UPDATE ON user_profiles FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_saved_addresses_updated_at BEFORE UPDATE ON saved_addresses FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_support_tickets_updated_at BEFORE UPDATE ON support_tickets FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE ride_schedules IS 'Pre-scheduled rides for future dates/times';
COMMENT ON TABLE payment_methods IS 'User saved payment methods including wallet, UPI, cards';
COMMENT ON TABLE wallet_transactions IS 'CabEase wallet transaction history';
COMMENT ON TABLE driver_profiles IS 'Enhanced driver profiles with verification and ratings';
COMMENT ON TABLE promo_codes IS 'Promotional discount codes and offers';
COMMENT ON TABLE notifications IS 'In-app notifications for users';
COMMENT ON TABLE sos_alerts IS 'Emergency SOS alerts with location tracking';
COMMENT ON TABLE user_profiles IS 'Enhanced user profiles with preferences';
COMMENT ON TABLE support_tickets IS 'Customer support ticket system';
COMMENT ON TABLE ride_tracking IS 'Real-time GPS tracking data for active rides';
COMMENT ON TABLE fare_rules IS 'Dynamic fare calculation rules';
