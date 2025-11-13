-- Fix all bookings with NULL coordinates
-- Update pickup coordinates based on location names
UPDATE bookings SET pickup_latitude = 13.0850, pickup_longitude = 80.2101 WHERE pickup_location = 'Anna Nagar' AND pickup_latitude IS NULL;
UPDATE bookings SET pickup_latitude = 13.0418, pickup_longitude = 80.2341 WHERE pickup_location = 'T Nagar' AND pickup_latitude IS NULL;
UPDATE bookings SET pickup_latitude = 13.0067, pickup_longitude = 80.2570 WHERE pickup_location = 'Adyar' AND pickup_latitude IS NULL;
UPDATE bookings SET pickup_latitude = 13.0067, pickup_longitude = 80.2042 WHERE pickup_location = 'Adambakkam' AND pickup_latitude IS NULL;
UPDATE bookings SET pickup_latitude = 13.0339, pickup_longitude = 80.2502 WHERE pickup_location = 'Alwarpet' AND pickup_latitude IS NULL;
UPDATE bookings SET pickup_latitude = 13.1143, pickup_longitude = 80.1619 WHERE pickup_location = 'Ambattur' AND pickup_latitude IS NULL;
UPDATE bookings SET pickup_latitude = 13.0732, pickup_longitude = 80.2609 WHERE pickup_location = 'Egmore' AND pickup_latitude IS NULL;
UPDATE bookings SET pickup_latitude = 13.0067, pickup_longitude = 80.2206 WHERE pickup_location = 'Guindy' AND pickup_latitude IS NULL;
UPDATE bookings SET pickup_latitude = 13.0525, pickup_longitude = 80.2252 WHERE pickup_location = 'Kodambakkam' AND pickup_latitude IS NULL;
UPDATE bookings SET pickup_latitude = 13.0732, pickup_longitude = 80.1948 WHERE pickup_location = 'Koyambedu' AND pickup_latitude IS NULL;
UPDATE bookings SET pickup_latitude = 13.0339, pickup_longitude = 80.2671 WHERE pickup_location = 'Mylapore' AND pickup_latitude IS NULL;
UPDATE bookings SET pickup_latitude = 13.0358, pickup_longitude = 80.2482 WHERE pickup_location = 'Teynampet' AND pickup_latitude IS NULL;
UPDATE bookings SET pickup_latitude = 12.9756, pickup_longitude = 80.2169 WHERE pickup_location = 'Velachery' AND pickup_latitude IS NULL;
UPDATE bookings SET pickup_latitude = 13.0015, pickup_longitude = 80.2669 WHERE pickup_location = 'Besant Nagar' AND pickup_latitude IS NULL;

-- Update drop coordinates based on location names
UPDATE bookings SET drop_latitude = 13.0850, drop_longitude = 80.2101 WHERE drop_location = 'Anna Nagar' AND drop_latitude IS NULL;
UPDATE bookings SET drop_latitude = 13.0418, drop_longitude = 80.2341 WHERE drop_location = 'T Nagar' AND drop_latitude IS NULL;
UPDATE bookings SET drop_latitude = 13.0067, drop_longitude = 80.2570 WHERE drop_location = 'Adyar' AND drop_latitude IS NULL;
UPDATE bookings SET drop_latitude = 13.0067, drop_longitude = 80.2042 WHERE drop_location = 'Adambakkam' AND drop_latitude IS NULL;
UPDATE bookings SET drop_latitude = 13.0339, drop_longitude = 80.2502 WHERE drop_location = 'Alwarpet' AND drop_latitude IS NULL;
UPDATE bookings SET drop_latitude = 13.1143, drop_longitude = 80.1619 WHERE drop_location = 'Ambattur' AND drop_latitude IS NULL;
UPDATE bookings SET drop_latitude = 13.0732, drop_longitude = 80.2609 WHERE drop_location = 'Egmore' AND drop_latitude IS NULL;
UPDATE bookings SET drop_latitude = 13.0067, drop_longitude = 80.2206 WHERE drop_location = 'Guindy' AND drop_latitude IS NULL;
UPDATE bookings SET drop_latitude = 13.0525, drop_longitude = 80.2252 WHERE drop_location = 'Kodambakkam' AND drop_latitude IS NULL;
UPDATE bookings SET drop_latitude = 13.0732, drop_longitude = 80.1948 WHERE drop_location = 'Koyambedu' AND drop_latitude IS NULL;
UPDATE bookings SET drop_latitude = 13.0339, drop_longitude = 80.2671 WHERE drop_location = 'Mylapore' AND drop_latitude IS NULL;
UPDATE bookings SET drop_latitude = 13.0358, drop_longitude = 80.2482 WHERE drop_location = 'Teynampet' AND drop_latitude IS NULL;
UPDATE bookings SET drop_latitude = 12.9756, drop_longitude = 80.2169 WHERE drop_location = 'Velachery' AND drop_latitude IS NULL;
UPDATE bookings SET drop_latitude = 13.0015, drop_longitude = 80.2669 WHERE drop_location = 'Besant Nagar' AND drop_latitude IS NULL;

-- Show results
SELECT id, pickup_location, drop_location, 
       CASE WHEN pickup_latitude IS NULL THEN 'NULL' ELSE 'FIXED' END as pickup_status,
       CASE WHEN drop_latitude IS NULL THEN 'NULL' ELSE 'FIXED' END as drop_status
FROM bookings 
ORDER BY id DESC 
LIMIT 10;
