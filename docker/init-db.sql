-- Initialize roles and extensions for CabEase
-- Create extensions and default schemas if needed

-- Example: create postgis if needed (commented out by default)
-- CREATE EXTENSION IF NOT EXISTS postgis;

-- Create a dedicated schema (optional)
CREATE SCHEMA IF NOT EXISTS cabease AUTHORIZATION CURRENT_USER;

-- Optional: set timezone
ALTER DATABASE CURRENT SET timezone TO 'UTC';
