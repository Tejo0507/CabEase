@echo off
REM CabEase - Complete Shutdown Script
REM This script stops the CabEase application and Docker containers

echo ========================================
echo   CabEase - Complete Shutdown
echo ========================================
echo.

REM Stop Java processes
echo [1/2] Stopping CabEase application...
for /f "tokens=2" %%i in ('tasklist ^| findstr /i "java.exe"') do (
    taskkill /PID %%i /F >nul 2>&1
)
echo  CabEase application stopped
echo.

REM Stop Docker containers
echo [2/2] Stopping Docker containers...
cd /d "%~dp0"
docker-compose down
if errorlevel 1 (
    echo ERROR: Failed to stop Docker containers!
    pause
    exit /b 1
)
echo  Docker containers stopped
echo.

echo ========================================
echo   Shutdown Complete!
echo ========================================
echo   All services have been stopped.
echo ========================================
echo.

pause
