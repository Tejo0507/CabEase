# CabEase Production Startup Script
# This script sets up and launches CabEase with PostgreSQL

param(
    [switch]$UseDocker = $false,
    [switch]$SkipBuild = $false,
    [string]$DbHost = "localhost",
    [string]$DbPort = "5432",
    [string]$DbName = "cabease",
    [string]$DbUser = "cabease",
    [string]$DbPassword = "cabease_pass"
)

Write-Host "═══════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "   CabEase Production Database Setup & Launch" -ForegroundColor Cyan
Write-Host "═══════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

# Function to check if a command exists
function Test-Command {
    param($Command)
    try {
        if (Get-Command $Command -ErrorAction Stop) { return $true }
    } catch {
        return $false
    }
}

# Step 1: Check prerequisites
Write-Host "[1/6] Checking prerequisites..." -ForegroundColor Yellow

if (-not (Test-Command "java")) {
    Write-Host "  ✗ Java not found! Please install Java 11 or higher." -ForegroundColor Red
    exit 1
} else {
    $javaVersion = java -version 2>&1 | Select-String -Pattern "version" | Out-String
    Write-Host "  ✓ Java found: $($javaVersion.Trim())" -ForegroundColor Green
}

if (-not (Test-Command "mvn")) {
    Write-Host "  ✗ Maven not found! Please install Apache Maven." -ForegroundColor Red
    exit 1
} else {
    Write-Host "  ✓ Maven found" -ForegroundColor Green
}

# Step 2: Database setup
Write-Host ""
Write-Host "[2/6] Setting up database..." -ForegroundColor Yellow

if ($UseDocker) {
    if (-not (Test-Command "docker")) {
        Write-Host "  ✗ Docker not found! Please install Docker Desktop or use -UseDocker:`$false" -ForegroundColor Red
        exit 1
    }
    
    # Check if Docker is running
    try {
        docker ps | Out-Null
        Write-Host "  ✓ Docker is running" -ForegroundColor Green
    } catch {
        Write-Host "  ✗ Docker Desktop is not running! Please start Docker Desktop." -ForegroundColor Red
        Write-Host "    Alternative: Run this script with -UseDocker:`$false and set up PostgreSQL manually" -ForegroundColor Yellow
        exit 1
    }
    
    # Start PostgreSQL with Docker Compose
    Write-Host "  ⟳ Starting PostgreSQL and pgAdmin containers..." -ForegroundColor Cyan
    docker-compose up -d
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  ✓ PostgreSQL container started successfully" -ForegroundColor Green
        Write-Host "  ⟳ Waiting for PostgreSQL to be ready..." -ForegroundColor Cyan
        Start-Sleep -Seconds 10
        
        # Test connection
        $retries = 0
        while ($retries -lt 30) {
            try {
                docker exec cabease-postgres pg_isready -U $DbUser -d $DbName 2>$null | Out-Null
                if ($LASTEXITCODE -eq 0) {
                    Write-Host "  ✓ PostgreSQL is ready!" -ForegroundColor Green
                    break
                }
            } catch {}
            $retries++
            Start-Sleep -Seconds 2
        }
        
        if ($retries -eq 30) {
            Write-Host "  ⚠ PostgreSQL took too long to start, but continuing anyway..." -ForegroundColor Yellow
        }
    } else {
        Write-Host "  ✗ Failed to start Docker containers!" -ForegroundColor Red
        Write-Host "    Run 'docker-compose logs' to see error details" -ForegroundColor Yellow
        exit 1
    }
} else {
    Write-Host "  ⚠ Docker mode disabled - assuming PostgreSQL is already running at $DbHost`:$DbPort" -ForegroundColor Yellow
    Write-Host "    Database: $DbName" -ForegroundColor Gray
    Write-Host "    User: $DbUser" -ForegroundColor Gray
}

# Step 3: Set environment variables
Write-Host ""
Write-Host "[3/6] Configuring environment..." -ForegroundColor Yellow

$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://$DbHost`:$DbPort/$DbName"
$env:SPRING_DATASOURCE_USERNAME = $DbUser
$env:SPRING_DATASOURCE_PASSWORD = $DbPassword

Write-Host "  ✓ Environment variables set:" -ForegroundColor Green
Write-Host "    SPRING_DATASOURCE_URL = $env:SPRING_DATASOURCE_URL" -ForegroundColor Gray
Write-Host "    SPRING_DATASOURCE_USERNAME = $env:SPRING_DATASOURCE_USERNAME" -ForegroundColor Gray
Write-Host "    SPRING_DATASOURCE_PASSWORD = ********" -ForegroundColor Gray

# Step 4: Build application
if (-not $SkipBuild) {
    Write-Host ""
    Write-Host "[4/6] Building CabEase application..." -ForegroundColor Yellow
    
    mvn clean package -DskipTests -q
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  ✓ Build successful!" -ForegroundColor Green
    } else {
        Write-Host "  ✗ Build failed!" -ForegroundColor Red
        Write-Host "    Run 'mvn clean package' to see detailed errors" -ForegroundColor Yellow
        exit 1
    }
} else {
    Write-Host ""
    Write-Host "[4/6] Skipping build (using existing JAR)..." -ForegroundColor Yellow
}

# Step 5: Run application
Write-Host ""
Write-Host "[5/6] Starting CabEase application..." -ForegroundColor Yellow
Write-Host ""
Write-Host "═══════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "  Application will start with production profile" -ForegroundColor White
Write-Host "  Flyway will automatically:" -ForegroundColor White
Write-Host "    ✓ Create database schema (tables, indexes, constraints)" -ForegroundColor Green
Write-Host "    ✓ Add performance optimizations (triggers, views)" -ForegroundColor Green
Write-Host "    ✓ Seed sample data (users, drivers, cabs, bookings)" -ForegroundColor Green
Write-Host "═══════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""
Write-Host "  Access URLs:" -ForegroundColor Yellow
Write-Host "    • CabEase App:  http://localhost:8080" -ForegroundColor Cyan
Write-Host "    • Actuator:     http://localhost:8080/actuator/health" -ForegroundColor Cyan
if ($UseDocker) {
    Write-Host "    • pgAdmin:      http://localhost:8081 (admin@cabease.local / admin)" -ForegroundColor Cyan
}
Write-Host ""
Write-Host "  Default login credentials:" -ForegroundColor Yellow
Write-Host "    • Username: admin" -ForegroundColor Cyan
Write-Host "    • Password: password123" -ForegroundColor Cyan
Write-Host ""
Write-Host "═══════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

# Find the JAR file
$jarFile = Get-ChildItem -Path "target" -Filter "cabease-*.jar" -Exclude "*-original*" | Select-Object -First 1

if (-not $jarFile) {
    Write-Host "  ✗ JAR file not found in target/ directory!" -ForegroundColor Red
    exit 1
}

Write-Host "  ⟳ Launching: $($jarFile.Name)" -ForegroundColor Cyan
Write-Host ""

# Run the application
try {
    java -jar "-Dspring.profiles.active=prod" "target/$($jarFile.Name)"
} catch {
    Write-Host ""
    Write-Host "  ✗ Application stopped!" -ForegroundColor Red
} finally {
    Write-Host ""
    Write-Host "═══════════════════════════════════════════════════════" -ForegroundColor Cyan
    Write-Host "  Shutdown complete" -ForegroundColor Yellow
    Write-Host "═══════════════════════════════════════════════════════" -ForegroundColor Cyan
    
    if ($UseDocker) {
        Write-Host ""
        $response = Read-Host "Stop Docker containers? (y/N)"
        if ($response -eq 'y' -or $response -eq 'Y') {
            Write-Host "  ⟳ Stopping containers..." -ForegroundColor Cyan
            docker-compose down
            Write-Host "  ✓ Containers stopped" -ForegroundColor Green
        } else {
            Write-Host "  ℹ Containers still running. Use 'docker-compose down' to stop them." -ForegroundColor Cyan
        }
    }
}
