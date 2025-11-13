-- V3: Seed initial data for testing and demonstration
-- Adds sample users, drivers, cabs, and some test bookings

-- Insert admin and test users
-- Note: Password is 'password123' hashed with BCrypt
INSERT INTO users (username, name, password, email, role, created_at) VALUES
('admin', 'System Administrator', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@cabease.com', 'ROLE_ADMIN', CURRENT_TIMESTAMP),
('john.doe', 'John Doe', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'john.doe@example.com', 'ROLE_USER', CURRENT_TIMESTAMP),
('jane.smith', 'Jane Smith', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'jane.smith@example.com', 'ROLE_USER', CURRENT_TIMESTAMP),
('bob.wilson', 'Bob Wilson', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'bob.wilson@example.com', 'ROLE_USER', CURRENT_TIMESTAMP),
('alice.johnson', 'Alice Johnson', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'alice.johnson@example.com', 'ROLE_USER', CURRENT_TIMESTAMP);

-- Insert drivers
INSERT INTO drivers (name, license_number, phone_number, email, available, created_at) VALUES
('Rajesh Kumar', 'DL1420110012345', '+919876543210', 'rajesh.kumar@cabease.com', true, CURRENT_TIMESTAMP),
('Priya Singh', 'DL1420110012346', '+919876543211', 'priya.singh@cabease.com', true, CURRENT_TIMESTAMP),
('Mohammed Aslam', 'DL1420110012347', '+919876543212', 'mohammed.aslam@cabease.com', true, CURRENT_TIMESTAMP),
('Lakshmi Devi', 'DL1420110012348', '+919876543213', 'lakshmi.devi@cabease.com', true, CURRENT_TIMESTAMP),
('Amit Sharma', 'DL1420110012349', '+919876543214', 'amit.sharma@cabease.com', true, CURRENT_TIMESTAMP),
('Neha Gupta', 'DL1420110012350', '+919876543215', 'neha.gupta@cabease.com', false, CURRENT_TIMESTAMP),
('Suresh Reddy', 'DL1420110012351', '+919876543216', 'suresh.reddy@cabease.com', true, CURRENT_TIMESTAMP),
('Divya Nair', 'DL1420110012352', '+919876543217', 'divya.nair@cabease.com', true, CURRENT_TIMESTAMP),
('Vikram Patel', 'DL1420110012353', '+919876543218', 'vikram.patel@cabease.com', true, CURRENT_TIMESTAMP),
('Pooja Verma', 'DL1420110012354', '+919876543219', 'pooja.verma@cabease.com', true, CURRENT_TIMESTAMP);

-- Insert cabs with detailed specifications
INSERT INTO cabs (
    cab_number, model, brand, vehicle_type, capacity, price_per_km, 
    available, driver_id, current_latitude, current_longitude, 
    color, year, fuel_type, ac_available, music_system, gps_enabled, 
    verified, average_rating, total_trips, total_earnings, created_at
) VALUES
-- Sedans
('KA01AB1234', 'Swift Dzire', 'Maruti Suzuki', 'SEDAN', 4, 12.50, true, 1, 12.9716, 77.5946, 'White', 2022, 'PETROL', true, true, true, true, 4.5, 150, 45000.00, CURRENT_TIMESTAMP),
('KA02CD5678', 'Verna', 'Hyundai', 'SEDAN', 4, 13.00, true, 2, 12.9352, 77.6245, 'Silver', 2023, 'DIESEL', true, true, true, true, 4.7, 200, 65000.00, CURRENT_TIMESTAMP),
('KA03EF9012', 'City', 'Honda', 'SEDAN', 4, 13.50, true, 3, 12.9141, 77.6112, 'Black', 2023, 'PETROL', true, true, true, true, 4.6, 180, 58500.00, CURRENT_TIMESTAMP),
('TN01GH3456', 'Xcent', 'Hyundai', 'SEDAN', 4, 11.50, true, 7, 13.0827, 80.2707, 'Blue', 2021, 'DIESEL', true, false, true, true, 4.3, 120, 35000.00, CURRENT_TIMESTAMP),

-- SUVs
('KA04IJ7890', 'Innova Crysta', 'Toyota', 'SUV', 7, 18.00, true, 4, 12.9698, 77.7500, 'Grey', 2023, 'DIESEL', true, true, true, true, 4.8, 250, 112500.00, CURRENT_TIMESTAMP),
('KA05KL2345', 'Ertiga', 'Maruti Suzuki', 'SUV', 7, 15.00, true, 5, 12.8406, 77.6644, 'White', 2022, 'PETROL', true, true, true, true, 4.4, 160, 60000.00, CURRENT_TIMESTAMP),
('TN02MN6789', 'Fortuner', 'Toyota', 'SUV', 7, 25.00, true, 8, 13.0415, 80.2337, 'Black', 2024, 'DIESEL', true, true, true, true, 4.9, 80, 50000.00, CURRENT_TIMESTAMP),

-- Hatchbacks (Budget)
('KA06OP0123', 'Swift', 'Maruti Suzuki', 'HATCHBACK', 4, 10.00, true, 6, 12.9538, 77.5713, 'Red', 2021, 'PETROL', true, false, true, true, 4.2, 90, 22500.00, CURRENT_TIMESTAMP),
('KA07QR4567', 'i20', 'Hyundai', 'HATCHBACK', 4, 11.00, false, NULL, 12.9279, 77.6271, 'Orange', 2022, 'PETROL', true, true, true, true, 4.5, 140, 38500.00, CURRENT_TIMESTAMP),
('KA08ST8901', 'Baleno', 'Maruti Suzuki', 'HATCHBACK', 4, 10.50, true, 9, 12.9611, 77.6387, 'White', 2023, 'PETROL', true, true, true, true, 4.6, 110, 28875.00, CURRENT_TIMESTAMP),

-- Luxury
('KA09UV2345', 'E-Class', 'Mercedes-Benz', 'LUXURY', 4, 35.00, true, 10, 12.9719, 77.6412, 'Black', 2024, 'DIESEL', true, true, true, true, 5.0, 45, 39375.00, CURRENT_TIMESTAMP),
('KA10WX6789', 'Audi A4', 'Audi', 'LUXURY', 4, 32.00, false, NULL, 12.9352, 77.6245, 'Blue', 2023, 'PETROL', true, true, true, true, 4.9, 30, 24000.00, CURRENT_TIMESTAMP);

-- Sample completed bookings for demonstration
INSERT INTO bookings (
    pickup_location, drop_location, pickup_latitude, pickup_longitude, 
    drop_latitude, drop_longitude, booking_date_time, distance, 
    base_fare, total_fare, status, user_id, cab_id, payment_method, 
    payment_status, trip_started_at, trip_ended_at, user_rating, 
    driver_rating, created_at
) VALUES
-- Completed trips
('MG Road, Bangalore', 'Koramangala, Bangalore', 12.9716, 77.5946, 12.9352, 77.6245, 
 CURRENT_TIMESTAMP - INTERVAL '5 days', 8.5, 106.25, 106.25, 'COMPLETED', 2, 1, 
 'CARD', 'PAID', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days' + INTERVAL '25 minutes', 5, 5, CURRENT_TIMESTAMP - INTERVAL '5 days'),

('Electronic City, Bangalore', 'Whitefield, Bangalore', 12.8406, 77.6644, 12.9698, 77.7500, 
 CURRENT_TIMESTAMP - INTERVAL '4 days', 22.3, 401.40, 401.40, 'COMPLETED', 3, 5, 
 'UPI', 'PAID', CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '4 days' + INTERVAL '55 minutes', 4, 5, CURRENT_TIMESTAMP - INTERVAL '4 days'),

('Indiranagar, Bangalore', 'Airport, Bangalore', 12.9719, 77.6412, 13.1986, 77.7066, 
 CURRENT_TIMESTAMP - INTERVAL '3 days', 32.5, 1137.50, 1137.50, 'COMPLETED', 4, 11, 
 'CASH', 'PAID', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days' + INTERVAL '75 minutes', 5, 5, CURRENT_TIMESTAMP - INTERVAL '3 days'),

('Jayanagar, Bangalore', 'HSR Layout, Bangalore', 12.9141, 77.6112, 12.9087, 77.6467, 
 CURRENT_TIMESTAMP - INTERVAL '2 days', 5.2, 67.60, 67.60, 'COMPLETED', 5, 3, 
 'CARD', 'PAID', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days' + INTERVAL '18 minutes', 4, 4, CURRENT_TIMESTAMP - INTERVAL '2 days'),

('Marathahalli, Bangalore', 'Yelahanka, Bangalore', 12.9611, 77.6387, 13.1007, 77.5963, 
 CURRENT_TIMESTAMP - INTERVAL '1 day', 18.7, 196.35, 196.35, 'COMPLETED', 2, 10, 
 'UPI', 'PAID', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day' + INTERVAL '45 minutes', 5, 5, CURRENT_TIMESTAMP - INTERVAL '1 day'),

-- Active/Pending bookings
('Hebbal, Bangalore', 'Malleswaram, Bangalore', 13.0346, 77.5977, 13.0055, 77.5707, 
 CURRENT_TIMESTAMP - INTERVAL '2 hours', 7.8, 97.50, 97.50, 'IN_PROGRESS', 3, 2, 
 'CARD', 'PENDING', CURRENT_TIMESTAMP - INTERVAL '2 hours', NULL, NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '2 hours'),

('BTM Layout, Bangalore', 'JP Nagar, Bangalore', 12.9165, 77.6101, 12.9082, 77.5855, 
 CURRENT_TIMESTAMP - INTERVAL '30 minutes', 4.2, 54.60, 54.60, 'PENDING', 4, NULL, 
 'CASH', 'PENDING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP - INTERVAL '30 minutes'),

('Richmond Town, Bangalore', 'Koramangala, Bangalore', 12.9738, 77.6082, 12.9352, 77.6245, 
 CURRENT_TIMESTAMP + INTERVAL '2 hours', 6.5, 117.00, 117.00, 'SCHEDULED', 5, NULL, 
 'UPI', 'PENDING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP);

-- Sample feedback for completed trips
INSERT INTO feedbacks (rating, comment, user_id, booking_id, created_at) VALUES
(5, 'Excellent service! Driver was very professional and the car was clean.', 2, 1, CURRENT_TIMESTAMP - INTERVAL '5 days'),
(4, 'Good ride, but traffic made it a bit longer than expected.', 3, 2, CURRENT_TIMESTAMP - INTERVAL '4 days'),
(5, 'Luxury car experience was amazing! Highly recommended.', 4, 3, CURRENT_TIMESTAMP - INTERVAL '3 days'),
(4, 'Quick and comfortable ride. Driver knew all the shortcuts.', 5, 4, CURRENT_TIMESTAMP - INTERVAL '2 days'),
(5, 'Perfect timing and great vehicle condition. Will use again!', 2, 5, CURRENT_TIMESTAMP - INTERVAL '1 day');

-- Update sequence values to avoid conflicts with manual inserts
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('drivers_id_seq', (SELECT MAX(id) FROM drivers));
SELECT setval('cabs_id_seq', (SELECT MAX(id) FROM cabs));
SELECT setval('bookings_id_seq', (SELECT MAX(id) FROM bookings));
SELECT setval('feedbacks_id_seq', (SELECT MAX(id) FROM feedbacks));
