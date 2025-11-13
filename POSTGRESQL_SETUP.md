# PostgreSQL Setup Guide for CabEase

## ⚠️ IMPORTANT: PostgreSQL is NOT installed on your system

You have two options:

---

## Option 1: Install PostgreSQL (Recommended for Production)

### Step 1: Download PostgreSQL
1. Visit: https://www.postgresql.org/download/windows/
2. Download the installer for Windows (latest stable version, e.g., PostgreSQL 15 or 16)
3. Run the installer

### Step 2: Installation Steps
1. **Installation Directory**: Use default (`C:\Program Files\PostgreSQL\15`)
2. **Components**: Select all (PostgreSQL Server, pgAdmin 4, Command Line Tools)
3. **Data Directory**: Use default
4. **Password**: Set a password for the `postgres` user (e.g., `postgres`)
   - ⚠️ **REMEMBER THIS PASSWORD!**
5. **Port**: Use default `5432`
6. **Locale**: Use default

### Step 3: Create CabEase Database
After installation, open PowerShell and run:

```powershell
# Add PostgreSQL to PATH (adjust version if different)
$env:Path += ";C:\Program Files\PostgreSQL\15\bin"

# Create the database
psql -U postgres -c "CREATE DATABASE cabease_db;"

# Verify database was created
psql -U postgres -c "\l" | Select-String "cabease_db"
```

### Step 4: Update application.properties
The configuration is already updated! Just verify the password in:
`src/main/resources/application.properties`

```properties
spring.datasource.username=postgres
spring.datasource.password=postgres  # Change this to your PostgreSQL password
```

### Step 5: Rebuild and Run
```powershell
mvn clean package -DskipTests
java -jar target/cabease-1.0.0.jar
```

---

## Option 2: Use Docker PostgreSQL (Quick Setup)

### Prerequisites
- Docker Desktop must be installed

### Step 1: Run PostgreSQL in Docker
```powershell
docker run --name cabease-postgres `
  -e POSTGRES_PASSWORD=postgres `
  -e POSTGRES_DB=cabease_db `
  -p 5432:5432 `
  -d postgres:15

# Verify container is running
docker ps | Select-String "cabease-postgres"
```

### Step 2: Verify Database
```powershell
docker exec -it cabease-postgres psql -U postgres -c "\l"
```

### Step 3: Application Configuration
No changes needed! The current settings in `application.properties` are:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cabease_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### Step 4: Rebuild and Run
```powershell
mvn clean package -DskipTests
java -jar target/cabease-1.0.0.jar
```

---

## Option 3: Temporarily Use H2 Until PostgreSQL is Ready

If you want to test immediately without installing PostgreSQL, we can revert to H2 temporarily.

### Revert to H2:
```powershell
# I'll update the configuration back to H2 if you choose this option
```

---

## Current Configuration Status

✅ **application.properties**: Updated to use PostgreSQL
✅ **pom.xml**: PostgreSQL driver already included
✅ **Cab.java**: Fixed reserved keyword issue
❌ **PostgreSQL**: NOT INSTALLED

---

## What Would You Like To Do?

**Option A**: Install PostgreSQL Desktop (Production-ready)
- Best for: Long-term development and production deployment
- Time: ~10 minutes to install

**Option B**: Use Docker PostgreSQL (Quick test)
- Best for: Quick testing, development
- Time: ~2 minutes to set up
- Requires: Docker Desktop

**Option C**: Revert to H2 temporarily
- Best for: Immediate testing without PostgreSQL
- Time: ~1 minute
- Note: Data will be lost on restart

---

## Recommended Next Steps

1. **Choose an option** from above
2. **Follow the setup steps** for your chosen option
3. **Update password** in `application.properties` if needed
4. **Rebuild** the application: `mvn clean package -DskipTests`
5. **Run** the application: `java -jar target/cabease-1.0.0.jar`
6. **Test** the booking functionality

---

## Database Configuration Details

### PostgreSQL Configuration (Current)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cabease_db
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### Benefits of PostgreSQL
✅ No reserved keyword issues (like H2's `year` problem)
✅ Data persists across restarts
✅ Production-ready
✅ Better performance at scale
✅ Advanced features and extensions

---

## Troubleshooting

### If PostgreSQL installation fails:
- Ensure you have admin rights
- Disable antivirus temporarily
- Check if port 5432 is available: `Test-NetConnection -ComputerName localhost -Port 5432`

### If connection fails:
- Verify PostgreSQL is running: `Get-Service -Name "*postgres*"`
- Check password is correct in `application.properties`
- Verify database exists: `psql -U postgres -c "\l"`

### If you see "password authentication failed":
- Update the password in `application.properties`
- Or reset PostgreSQL password using pgAdmin

---

**Let me know which option you prefer, and I'll help you proceed!**
