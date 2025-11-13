@echo off
REM CabEase - Complete Startup Script
REM This script starts Docker containers and the CabEase application

echo ========================================
echo   CabEase - Complete Startup
echo ========================================
echo.

REM Check if Docker Desktop is running
echo [1/4] Checking Docker Desktop status...
docker info >nul 2>&1
if errorlevel 1 (
    echo ERROR: Docker Desktop is not running!
    echo Please start Docker Desktop and try again.
    pause
    exit /b 1
)
echo  Docker Desktop is running
echo.

REM Start Docker containers
echo [2/4] Starting Docker containers...
docker-compose up -d
if errorlevel 1 (
    echo ERROR: Failed to start Docker containers!
    pause
    exit /b 1
)
echo  PostgreSQL and pgAdmin containers started
echo.

REM Wait for PostgreSQL to be ready
echo [3/4] Waiting for PostgreSQL to be ready...
timeout /t 10 /nobreak >nul
docker exec cabease-postgres pg_isready -U cabease >nul 2>&1
if errorlevel 1 (
    echo WARNING: PostgreSQL might not be ready yet. Waiting additional 10 seconds...
    timeout /t 10 /nobreak >nul
)
echo  PostgreSQL is ready
echo.

REM Start CabEase application
echo [4/4] Starting CabEase application...
echo.
echo ========================================
echo   Application Status:
echo ========================================
echo   PostgreSQL: http://localhost:5432
echo   pgAdmin:    http://localhost:8081
echo   CabEase:    http://localhost:8080
echo ========================================
echo.
echo Starting CabEase in 5 seconds...
echo Press Ctrl+C to cancel
timeout /t 5 /nobreak
echo.

cd /d "%~dp0"
java -Duser.timezone=UTC -jar target\cabease-1.0.0.jar

pause
