# 🚕 CabEase Application - Startup Guide

## 📋 Prerequisites
- Docker Desktop installed and running
- Java 23.0.2 (or compatible JDK)
- Maven for building (optional, JAR already built)

## 🚀 Quick Start

### 1. Start Docker Containers
```powershell
cd E:\CabEase
docker-compose up -d
```

This will start:
- **PostgreSQL 15.14** on port `5432`
- **pgAdmin 4** on port `8081`

### 2. Start CabEase Application
```powershell
# Option A: Using batch script (recommended)
cd E:\CabEase
.\start-cabease.bat

# Option B: Using Java directly
java -Duser.timezone=UTC -jar target\cabease-1.0.0.jar
```

### 3. Access the Application

#### Main Application
- **URL**: http://localhost:8080
- **Login Page**: http://localhost:8080/login
- **Health Check**: http://localhost:8080/actuator/health

#### Database Admin (pgAdmin)
- **URL**: http://localhost:8081
- **Email**: admin@cabease.com
- **Password**: admin123

## 👥 Test User Credentials

### Admin Account
- **Email**: admin@cabease.com
- **Password**: admin123
- **Role**: ROLE_ADMIN

### Regular User Accounts
1. **Test User**
   - **Email**: testuser@cabease.com
   - **Password**: user123
   - **Role**: ROLE_USER

2. **John Doe**
   - **Email**: john@example.com
   - **Password**: test123
   - **Role**: ROLE_USER

## 📊 Database Information

### Connection Details
- **Host**: localhost
- **Port**: 5432
- **Database**: cabease
- **Username**: cabease
- **Password**: cabease_pass

### Database Tables
1. **users** - User accounts (admin/user roles)
2. **drivers** - Driver profiles
3. **cabs** - Vehicle information
4. **bookings** - Booking records
5. **feedbacks** - User feedback
6. **flyway_schema_history** - Migration history

### Connect via psql
```powershell
docker exec -it cabease-postgres psql -U cabease -d cabease
```

Common queries:
```sql
-- List all tables
\dt

-- View users
SELECT id, username, email, role FROM users;

-- Check booking count
SELECT COUNT(*) FROM bookings;

-- View all cabs
SELECT id, cab_number, vehicle_type, available FROM cabs;
```

## 🛠️ Troubleshooting

### Application won't start
1. Check if port 8080 is already in use:
   ```powershell
   netstat -ano | findstr :8080
   ```

2. Check if Docker containers are running:
   ```powershell
   docker ps
   ```

3. View application logs:
   - Check terminal output where `start-cabease.bat` is running
   - Look for errors in Spring Boot startup sequence

### Database connection issues
1. Verify PostgreSQL container is running:
   ```powershell
   docker ps | findstr cabease-postgres
   ```

2. Test database connection:
   ```powershell
   docker exec cabease-postgres psql -U cabease -d cabease -c "SELECT version();"
   ```

3. Restart containers if needed:
   ```powershell
   docker-compose down
   docker-compose up -d
   ```

### Login not working
1. Verify users exist in database:
   ```powershell
   docker exec cabease-postgres psql -U cabease -d cabease -c "SELECT * FROM users;"
   ```

2. Clear browser cache and try again
3. Check credentials match exactly (email is case-sensitive)

## 🔄 Stopping the Application

### Stop CabEase Application
- Press `Ctrl+C` in the terminal running `start-cabease.bat`
- Or find and kill the Java process:
  ```powershell
  Get-Process -Name java | Where-Object {$_.MainWindowTitle -like "*cabease*"} | Stop-Process
  ```

### Stop Docker Containers
```powershell
cd E:\CabEase
docker-compose down
```

## 📦 Rebuilding the Application

If you make code changes:
```powershell
# Clean build
mvn clean package -DskipTests

# Then restart application
.\start-cabease.bat
```

## 🎯 Common Tasks

### View Docker Logs
```powershell
# PostgreSQL logs
docker logs cabease-postgres

# Follow logs in real-time
docker logs -f cabease-postgres
```

### Backup Database
```powershell
docker exec cabease-postgres pg_dump -U cabease cabease > backup_$(Get-Date -Format 'yyyyMMdd_HHmmss').sql
```

### Restore Database
```powershell
Get-Content backup_20250119_123456.sql | docker exec -i cabease-postgres psql -U cabease -d cabease
```

## 🌐 Available Endpoints

### Public Endpoints (no authentication required)
- `/` - Home page
- `/home` - Home page
- `/login` - Login page
- `/register` - Registration page
- `/error` - Error page
- `/h2-console/**` - H2 Console (if enabled)

### Protected Endpoints (authentication required)
- `/dashboard` - User dashboard
- `/booking-dashboard` - Booking management
- `/api/**` - REST API endpoints
- `/actuator/health` - Health check (when authorized)

## 📝 Notes

- **Default Profile**: Production (`prod`) profile is active
- **Timezone**: UTC configured for consistency
- **Connection Pool**: HikariCP with 5-30 connections
- **Security**: Spring Security with form-based login
- **Password Encryption**: BCrypt algorithm
- **Schema Management**: Hibernate validation mode (tables persist)

## 🎓 Development Tips

1. **Hot Reload**: Use Spring Boot DevTools for auto-restart during development
2. **Debug Mode**: Add `-Xdebug` to Java command for debugging
3. **Profile Selection**: Use `--spring.profiles.active=dev` for development profile
4. **Log Level**: Adjust logging in `application-prod.properties`

## 📞 Support

For issues or questions:
1. Check this guide first
2. Review application logs
3. Check Docker container status
4. Verify database connectivity

---

**Last Updated**: 2025-01-19
**Application Version**: 1.0.0
**Spring Boot Version**: 2.7.17
**PostgreSQL Version**: 15.14
