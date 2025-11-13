# 🚀 CabEase Quick Start Guide (After System Restart)

---

## 📋 Step-by-Step Instructions

### 1️⃣ Start Docker Desktop
- Open **Docker Desktop** application
- Wait until it shows "Docker is running"

### 2️⃣ Start PostgreSQL Database
```powershell
docker start cabease-postgres
```

**Verify it's running:**
```powershell
docker ps --filter name=cabease-postgres
```
You should see the container on port **5800**

### 3️⃣ Navigate to Project
```powershell
cd E:\CabEase
```

### 4️⃣ Run the Application
```powershell
java "-Duser.timezone=Asia/Kolkata" -jar target\cabease-1.0.0.jar
```

### 5️⃣ Open Browser
```
http://localhost:8080
```

---

## 🔑 Login Credentials

| Email | Password | Role |
|-------|----------|------|
| admin@cabease.com | admin123 | Admin |
| testuser@cabease.com | user123 | User |
| john@example.com | test123 | User |

---

## 🌐 Important URLs

- **Home**: http://localhost:8080/
- **Login**: http://localhost:8080/login
- **Dashboard**: http://localhost:8080/dashboard
- **Booking Dashboard**: http://localhost:8080/booking-dashboard
- **Ride Tracking**: http://localhost:8080/ride-tracking?rideId={id}

---

## ⚡ One-Command Startup

**Copy-paste this after system restart:**
```powershell
docker start cabease-postgres; Start-Sleep -Seconds 3; cd E:\CabEase; java "-Duser.timezone=Asia/Kolkata" -jar target\cabease-1.0.0.jar
```

---

## � Stop Application

**Stop Java Application:**
```powershell
Ctrl+C
```
OR
```powershell
taskkill /F /IM java.exe
```

**Stop Database:**
```powershell
docker stop cabease-postgres
```

---

## � Rebuild (if code changes)

```powershell
cd E:\CabEase
mvn clean package -DskipTests
java "-Duser.timezone=Asia/Kolkata" -jar target\cabease-1.0.0.jar
```

---

## ⚠️ Troubleshooting

### Database not starting?
```powershell
# Recreate container
docker run -d --name cabease-postgres -e POSTGRES_USER=cabease -e POSTGRES_PASSWORD=cabease -e POSTGRES_DB=cabease -p 5800:5432 postgres:15-alpine
```

### Port 8080 in use?
```powershell
taskkill /F /IM java.exe
```

### Application error?
```powershell
mvn clean package -DskipTests
java "-Duser.timezone=Asia/Kolkata" -jar target\cabease-1.0.0.jar
```

---

## 📝 Technical Details

- **Database**: PostgreSQL 15 on Docker (port 5800)
- **App Port**: 8080
- **Timezone**: Asia/Kolkata
- **Framework**: Spring Boot 2.7.17
- **Google Maps API**: Configured
- **Sample Data**: 100 drivers pre-loaded
