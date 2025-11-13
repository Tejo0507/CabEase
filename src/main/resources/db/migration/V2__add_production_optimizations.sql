-- V2: Production optimizations and additional constraints
-- Adds performance indexes, triggers, and database functions

-- Add audit columns tracking
ALTER TABLE users ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE drivers ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE drivers ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Create updated_at trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Attach triggers to all tables
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_drivers_updated_at BEFORE UPDATE ON drivers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_cabs_updated_at BEFORE UPDATE ON cabs
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_bookings_updated_at BEFORE UPDATE ON bookings
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Additional performance indexes
CREATE INDEX IF NOT EXISTS idx_cab_driver_id ON cabs (driver_id);
CREATE INDEX IF NOT EXISTS idx_cab_verified ON cabs (verified);
CREATE INDEX IF NOT EXISTS idx_cab_rating ON cabs (average_rating DESC);
CREATE INDEX IF NOT EXISTS idx_booking_cab_id ON bookings (cab_id);
CREATE INDEX IF NOT EXISTS idx_booking_payment_status ON bookings (payment_status);
CREATE INDEX IF NOT EXISTS idx_booking_trip_dates ON bookings (trip_started_at, trip_ended_at);
CREATE INDEX IF NOT EXISTS idx_driver_available ON drivers (available);
CREATE INDEX IF NOT EXISTS idx_user_email ON users (email);

-- Composite indexes for common queries
CREATE INDEX IF NOT EXISTS idx_booking_user_status ON bookings (user_id, status);
CREATE INDEX IF NOT EXISTS idx_cab_type_available ON cabs (vehicle_type, available);
CREATE INDEX IF NOT EXISTS idx_booking_status_scheduled ON bookings (status, scheduled_for) WHERE is_scheduled = true;

-- Add check constraints for data integrity
ALTER TABLE feedbacks ADD CONSTRAINT chk_rating_range CHECK (rating BETWEEN 1 AND 5);
ALTER TABLE cabs ADD CONSTRAINT chk_capacity_positive CHECK (capacity > 0);
ALTER TABLE cabs ADD CONSTRAINT chk_price_positive CHECK (price_per_km > 0);
ALTER TABLE bookings ADD CONSTRAINT chk_distance_positive CHECK (distance >= 0);
ALTER TABLE bookings ADD CONSTRAINT chk_fare_positive CHECK (total_fare >= 0);
ALTER TABLE bookings ADD CONSTRAINT chk_passenger_count CHECK (passenger_count IS NULL OR passenger_count > 0);

-- Create view for available cabs with driver info
CREATE OR REPLACE VIEW v_available_cabs AS
SELECT 
    c.id,
    c.cab_number,
    c.model,
    c.brand,
    c.vehicle_type,
    c.capacity,
    c.price_per_km,
    c.current_latitude,
    c.current_longitude,
    c.color,
    c.year,
    c.ac_available,
    c.music_system,
    c.gps_enabled,
    c.average_rating,
    c.total_trips,
    d.id as driver_id,
    d.name as driver_name,
    d.phone_number as driver_phone,
    d.license_number
FROM cabs c
LEFT JOIN drivers d ON c.driver_id = d.id
WHERE c.available = true AND c.verified = true
  AND (d.id IS NULL OR d.available = true);

-- Create view for booking statistics
CREATE OR REPLACE VIEW v_booking_stats AS
SELECT 
    DATE(booking_date_time) as booking_date,
    COUNT(*) as total_bookings,
    COUNT(*) FILTER (WHERE status = 'COMPLETED') as completed_bookings,
    COUNT(*) FILTER (WHERE status = 'CANCELLED') as cancelled_bookings,
    AVG(total_fare) FILTER (WHERE status = 'COMPLETED') as avg_fare,
    SUM(total_fare) FILTER (WHERE status = 'COMPLETED') as total_revenue,
    AVG(distance) FILTER (WHERE status = 'COMPLETED') as avg_distance
FROM bookings
GROUP BY DATE(booking_date_time);

-- Create materialized view for driver performance (refresh periodically)
CREATE MATERIALIZED VIEW IF NOT EXISTS mv_driver_performance AS
SELECT 
    d.id as driver_id,
    d.name as driver_name,
    d.license_number,
    c.id as cab_id,
    c.cab_number,
    COUNT(b.id) as total_bookings,
    COUNT(b.id) FILTER (WHERE b.status = 'COMPLETED') as completed_trips,
    AVG(b.driver_rating) FILTER (WHERE b.driver_rating IS NOT NULL) as avg_rating,
    SUM(b.total_fare) FILTER (WHERE b.status = 'COMPLETED') as total_earnings,
    AVG(b.total_fare) FILTER (WHERE b.status = 'COMPLETED') as avg_fare_per_trip,
    MAX(b.trip_ended_at) as last_trip_date
FROM drivers d
LEFT JOIN cabs c ON d.id = c.driver_id
LEFT JOIN bookings b ON c.id = b.cab_id
GROUP BY d.id, d.name, d.license_number, c.id, c.cab_number;

-- Create index on materialized view
CREATE UNIQUE INDEX IF NOT EXISTS idx_mv_driver_perf_driver ON mv_driver_performance (driver_id, cab_id);

-- Comments for documentation
COMMENT ON TABLE users IS 'Application users - customers and admins';
COMMENT ON TABLE drivers IS 'Registered drivers with license information';
COMMENT ON TABLE cabs IS 'Vehicle inventory with detailed specifications';
COMMENT ON TABLE bookings IS 'All cab booking requests and trip records';
COMMENT ON TABLE feedbacks IS 'User feedback and ratings for completed trips';
COMMENT ON VIEW v_available_cabs IS 'Real-time view of available cabs with driver details';
COMMENT ON VIEW v_booking_stats IS 'Daily booking statistics and revenue metrics';
COMMENT ON MATERIALIZED VIEW mv_driver_performance IS 'Driver performance metrics - refresh periodically';
