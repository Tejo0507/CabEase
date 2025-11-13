# 🚀 CabEase Quick Reference Card

## ⚡ Quick Start
```powershell
cd E:\CabEase
.\start-all.bat
```

## 🌐 Access URLs
| Service | URL | Credentials |
|---------|-----|-------------|
| **CabEase** | http://localhost:8080 | admin@cabease.com / admin123 |
| **Login** | http://localhost:8080/login | testuser@cabease.com / user123 |
| **Dashboard** | http://localhost:8080/dashboard | (after login) |
| **Booking** | http://localhost:8080/booking-dashboard | (Google Maps enabled) |
| **pgAdmin** | http://localhost:8081 | admin@cabease.com / admin123 |

## 📊 System Status
```powershell
# Check all services
docker ps

# Check application
Invoke-RestMethod http://localhost:8080/actuator/health

# Check database
docker exec cabease-postgres pg_isready -U cabease
```

## 🛠️ Common Commands
```powershell
# Start everything
.\start-all.bat

# Stop everything
.\stop-all.bat

# Restart application only
Get-Process -Name java | Stop-Process -Force
.\start-cabease.bat

# View logs
docker logs cabease-postgres
docker logs cabease-pgadmin

# Database access
docker exec -it cabease-postgres psql -U cabease -d cabease
```

## 🐛 Fixed Issues
✅ Circular dependency (Flyway ↔ EntityManagerFactory)  
✅ Spring profile not activating  
✅ Docker Compose version warning  
✅ pgAdmin email validation  
✅ Google Maps API configuration  

## 🗺️ Google Maps
- **API Key**: `AIzaSyCjyzQGcwNLjqnE-aF0uaWaaJbIhLUZ9Ss`
- **Used In**: booking-dashboard.html, dashboard.html
- **Libraries**: places, geometry
- **Status**: ✅ Configured and working

## 🗄️ Database
- **Host**: localhost:5432
- **Database**: cabease
- **User**: cabease
- **Password**: cabease_pass
- **Tables**: 6 (users, drivers, cabs, bookings, feedbacks, flyway_schema_history)

## 👥 Test Users
| Email | Password | Role |
|-------|----------|------|
| admin@cabease.com | admin123 | ADMIN |
| testuser@cabease.com | user123 | USER |
| john@example.com | test123 | USER |

## 📝 Important Files
- `start-all.bat` - Start everything
- `stop-all.bat` - Stop everything
- `start-cabease.bat` - Application only
- `FIXES_APPLIED.md` - Detailed fix report
- `STARTUP.md` - Complete startup guide

## ⚠️ Troubleshooting
**Port 8080 in use?**
```powershell
Get-Process -Name java | Stop-Process -Force
```

**Database not responding?**
```powershell
docker-compose restart postgres
```

**Application won't start?**
```powershell
mvn clean package -DskipTests
.\start-cabease.bat
```

---

**Status**: ✅ ALL SYSTEMS OPERATIONAL  
**Last Updated**: October 19, 2025
