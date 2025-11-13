@echo off
REM CabEase Startup Script for Windows
set JAVA_OPTS=-Duser.timezone=Asia/Kolkata
java %JAVA_OPTS% -jar target\cabease-1.0.0.jar
