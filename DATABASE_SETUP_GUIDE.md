# CabEase Production Database Setup Guide

## Overview
This guide will help you set up a fully functional PostgreSQL database for your CabEase project.

## Option 1: Local PostgreSQL with Docker (Recommended)

### Prerequisites
1. **Start Docker Desktop**
   - Open Docker Desktop application
   - Wait for it to fully start (check system tray icon)

### Setup Steps

1. **Start the Database**
   ```powershell
   cd E:\CabEase
   docker-compose up -d
   ```

2. **Verify Containers are Running**
   ```powershell
   docker ps
   ```
   You should see:
   - `cabease-postgres` (PostgreSQL 15)
   - `cabease-pgadmin` (pgAdmin web interface)

3. **Access pgAdmin**
   - Open browser: http://localhost:8081
   - Login credentials:
     - Email: `admin@cabease.local`
     - Password: `admin`

4. **Configure Server in pgAdmin**
   - Click "Add New Server"
   - General tab:
     - Name: `CabEase Local`
   - Connection tab:
     - Host: `postgres` (or `host.docker.internal`)
     - Port: `5432`
     - Database: `cabease`
     - Username: `cabease`
     - Password: `cabease_pass`

## Option 2: Local PostgreSQL Installation (No Docker)

### Download and Install
1. Download PostgreSQL 15 from: https://www.postgresql.org/download/windows/
2. Run the installer
3. Set password for postgres user (remember this!)
4. Accept default port: 5432
5. Install pgAdmin (included in installer)

### Create Database
1. Open pgAdmin
2. Connect to PostgreSQL (localhost:5432)
3. Right-click "Databases" → "Create" → "Database"
   - Database name: `cabease`
   - Owner: `postgres` (or create a new user `cabease`)

### Create User (Optional but Recommended)
```sql
CREATE USER cabease WITH PASSWORD 'cabease_pass';
GRANT ALL PRIVILEGES ON DATABASE cabease TO cabease;
ALTER DATABASE cabease OWNER TO cabease;
```

## Option 3: Cloud PostgreSQL (Production Ready)

### Providers
1. **AWS RDS** - https://aws.amazon.com/rds/postgresql/
2. **Azure Database for PostgreSQL** - https://azure.microsoft.com/en-us/products/postgresql/
3. **Google Cloud SQL** - https://cloud.google.com/sql/docs/postgres
4. **Heroku Postgres** - https://www.heroku.com/postgres (Free tier available)
5. **ElephantSQL** - https://www.elephantsql.com/ (Free tier: 20MB)
6. **Supabase** - https://supabase.com/ (Free tier with 500MB)

### Cloud Setup (Example: Supabase)
1. Sign up at https://supabase.com
2. Create new project
3. Note connection details from Settings → Database
4. Use connection string format:
   ```
   postgresql://postgres:[PASSWORD]@[HOST]:5432/postgres
   ```

## Configure CabEase Application

### Set Environment Variables

**Windows PowerShell:**
```powershell
# For Docker/Local setup
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/cabease"
$env:SPRING_DATASOURCE_USERNAME="cabease"
$env:SPRING_DATASOURCE_PASSWORD="cabease_pass"

# For cloud setup (example)
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://your-host.supabase.co:5432/postgres"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="your-password"
```

**Or create a `.env` file** (not committed to git):
```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/cabease
SPRING_DATASOURCE_USERNAME=cabease
SPRING_DATASOURCE_PASSWORD=cabease_pass
```

## Run CabEase with Production Database

### Build the Application
```powershell
mvn clean package -DskipTests
```

### Run with Production Profile
```powershell
java -jar -Dspring.profiles.active=prod target/cabease-1.0.0.jar
```

**Or with environment variables:**
```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### What Happens on First Run
1. **Flyway Migration Executes**
   - Creates `flyway_schema_history` table
   - Runs `V1__create_schema.sql`
   - Creates all tables: users, drivers, cabs, bookings, feedbacks
   - Creates indexes and foreign key constraints

2. **Database is Ready**
   - Schema is fully initialized
   - Application starts and connects
   - Ready to accept requests

## Verify Database Setup

### Check Tables in pgAdmin
```sql
-- List all tables
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public';

-- Check table structure
\d users
\d drivers
\d cabs
\d bookings
\d feedbacks

-- Verify indexes
SELECT indexname, tablename 
FROM pg_indexes 
WHERE schemaname = 'public';
```

### Test Connection from Application
1. Access: http://localhost:8080/actuator/health
2. Should show database status: UP

## Add Sample Data

### Option 1: Via pgAdmin
Run SQL scripts in Query Tool:
```sql
-- Sample users
INSERT INTO users (username, name, password, email, role) VALUES
('admin', 'Admin User', '$2a$10$...', 'admin@cabease.com', 'ROLE_ADMIN'),
('john', 'John Doe', '$2a$10$...', 'john@example.com', 'ROLE_USER');

-- Sample drivers
INSERT INTO drivers (name, license_number, phone_number, email, available) VALUES
('Rajesh Kumar', 'DL1234567890', '+919876543210', 'rajesh@cabease.com', true),
('Priya Singh', 'DL0987654321', '+919876543211', 'priya@cabease.com', true);

-- Sample cabs
INSERT INTO cabs (cab_number, model, brand, vehicle_type, capacity, price_per_km, available, driver_id) VALUES
('KA01AB1234', 'Swift Dzire', 'Maruti Suzuki', 'SEDAN', 4, 12.50, true, 1),
('KA02CD5678', 'Innova Crysta', 'Toyota', 'SUV', 7, 18.00, true, 2);
```

### Option 2: Via Application
Use the CabEase admin interface to add:
- Drivers
- Cabs
- Test bookings

## Database Backup & Maintenance

### Backup Database
```powershell
# Using Docker
docker exec cabease-postgres pg_dump -U cabease cabease > backup_$(Get-Date -Format 'yyyyMMdd_HHmmss').sql

# Using local PostgreSQL
pg_dump -U cabease -d cabease > backup_$(Get-Date -Format 'yyyyMMdd_HHmmss').sql
```

### Restore Database
```powershell
# Using Docker
Get-Content backup_20251019.sql | docker exec -i cabease-postgres psql -U cabease -d cabease

# Using local PostgreSQL
psql -U cabease -d cabease < backup_20251019.sql
```

## Production Optimizations

### Database Configuration (postgresql.conf)
```properties
# Connection pooling (already handled by HikariCP in app)
max_connections = 100

# Memory settings (adjust based on server RAM)
shared_buffers = 256MB
effective_cache_size = 1GB
work_mem = 16MB
maintenance_work_mem = 128MB

# Performance
random_page_cost = 1.1  # For SSD storage
effective_io_concurrency = 200
```

### Monitoring Queries
```sql
-- Active connections
SELECT count(*) FROM pg_stat_activity;

-- Slow queries
SELECT pid, now() - query_start as duration, query 
FROM pg_stat_activity 
WHERE state = 'active' 
ORDER BY duration DESC;

-- Table sizes
SELECT 
    schemaname, 
    tablename, 
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables 
WHERE schemaname = 'public' 
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

## Troubleshooting

### Connection Refused
- Check if PostgreSQL is running: `docker ps` or Windows Services
- Verify port 5432 is not blocked by firewall
- Check connection string format

### Authentication Failed
- Verify username/password in environment variables
- Check `pg_hba.conf` for authentication method
- For Docker: ensure `POSTGRES_PASSWORD` was set correctly

### Flyway Migration Fails
- Check database user has CREATE privileges
- Verify migration files are in `src/main/resources/db/migration/`
- Check flyway_schema_history table for errors

### Performance Issues
- Run `VACUUM ANALYZE;` regularly
- Check and create missing indexes
- Monitor slow queries with pg_stat_statements

## Security Best Practices

1. **Never commit passwords** to git
2. **Use environment variables** for all credentials
3. **Enable SSL** for production connections
4. **Regular backups** - automated daily backups
5. **Update regularly** - keep PostgreSQL patched
6. **Restrict access** - use firewall rules
7. **Strong passwords** - minimum 16 characters
8. **Audit logging** - enable PostgreSQL audit extension

## Next Steps

1. ✅ Start PostgreSQL (Docker or local)
2. ✅ Configure environment variables
3. ✅ Run CabEase with `prod` profile
4. ✅ Verify schema creation via pgAdmin
5. ✅ Add sample data
6. ✅ Test application functionality
7. ✅ Set up automated backups
8. ✅ Configure monitoring

---

**Your CabEase production database is now fully functional and ready for big-scale deployment!** 🚀
