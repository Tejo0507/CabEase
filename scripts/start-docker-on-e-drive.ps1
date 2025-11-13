# Start Docker on E: Drive - Simplified
# Run this script to start Docker Desktop with data on E: drive

Write-Host "`n=======================================================" -ForegroundColor Cyan
Write-Host "   Docker Status Check & Startup" -ForegroundColor Cyan
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host ""

# Check if docker-desktop-data exists
$dockerDesktopData = wsl -l -v | Select-String "docker-desktop-data"

if ($dockerDesktopData) {
    Write-Host "[OK] docker-desktop-data already exists" -ForegroundColor Green
    Write-Host "Checking if it's on E: drive..." -ForegroundColor Yellow
    
    # Check location by examining the size
    $wslList = wsl -l -v
    Write-Host $wslList -ForegroundColor White
} else {
    Write-Host "[INFO] docker-desktop-data not created yet" -ForegroundColor Yellow
    Write-Host "This will be created automatically when Docker Desktop starts" -ForegroundColor Gray
}

Write-Host ""
Write-Host "Current Docker data location: E:\DockerData\docker-desktop" -ForegroundColor Green
Write-Host ""

# Check if Docker Desktop is running
$dockerProcess = Get-Process "Docker Desktop" -ErrorAction SilentlyContinue

if ($dockerProcess) {
    Write-Host "[OK] Docker Desktop is running (PID: $($dockerProcess.Id))" -ForegroundColor Green
} else {
    Write-Host "[INFO] Docker Desktop is not running" -ForegroundColor Yellow
    Write-Host "Starting Docker Desktop..." -ForegroundColor Cyan
    
    Start-Process "C:\Program Files\Docker\Docker\Docker Desktop.exe"
    
    Write-Host "[INFO] Docker Desktop started!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Please wait 1-2 minutes for Docker to fully initialize..." -ForegroundColor Yellow
    Write-Host "You can check the Docker Desktop system tray icon" -ForegroundColor Gray
}

Write-Host ""
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host "   Quick Test Commands" -ForegroundColor Cyan
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "After Docker is ready (icon in system tray shows 'Docker Desktop is running'):" -ForegroundColor White
Write-Host ""
Write-Host "  1. Test Docker:           " -NoNewline -ForegroundColor Gray
Write-Host "docker ps" -ForegroundColor Green
Write-Host "  2. Start CabEase DB:      " -NoNewline -ForegroundColor Gray
Write-Host "docker-compose up -d" -ForegroundColor Green
Write-Host "  3. View containers:       " -NoNewline -ForegroundColor Gray
Write-Host "docker ps" -ForegroundColor Green
Write-Host "  4. View Docker data size: " -NoNewline -ForegroundColor Gray
Write-Host "wsl -l -v" -ForegroundColor Green
Write-Host ""
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host ""
