# Move Existing Docker to E: Drive
# This script moves your existing Docker installation data to E: drive

Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host "   Move Docker Data to E: Drive" -ForegroundColor Cyan
Write-Host "   Docker version 28.4.0 detected" -ForegroundColor Cyan
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host ""

# Check if running as Administrator
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
if (-not $isAdmin) {
    Write-Host "[WARNING] This script requires Administrator privileges!" -ForegroundColor Yellow
    Write-Host "  Please run PowerShell as Administrator and try again." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "  Right-click PowerShell -> Run as Administrator" -ForegroundColor White
    exit 1
}

Write-Host "[1/5] Checking current Docker installation..." -ForegroundColor Yellow

# Check Docker version
try {
    $dockerVersion = docker --version
    Write-Host "  [OK] $dockerVersion" -ForegroundColor Green
} catch {
    Write-Host "  [ERROR] Docker not found in PATH" -ForegroundColor Red
    Write-Host "  Please ensure Docker Desktop is installed" -ForegroundColor Yellow
    exit 1
}

# Check WSL distributions
Write-Host ""
Write-Host "[2/5] Checking current Docker WSL distributions..." -ForegroundColor Yellow
$wslList = wsl -l -v | Out-String
Write-Host $wslList -ForegroundColor White

# Get current locations
$dockerDesktop = wsl -l -v | Select-String "docker-desktop\s"
$dockerDesktopData = wsl -l -v | Select-String "docker-desktop-data\s"

if (-not $dockerDesktop -and -not $dockerDesktopData) {
    Write-Host "  [WARNING] Docker WSL distributions not found" -ForegroundColor Yellow
    Write-Host "  Your Docker might be using Hyper-V instead of WSL 2" -ForegroundColor Yellow
    Write-Host ""
    $response = Read-Host "Continue anyway? (y/N)"
    if ($response -ne 'y' -and $response -ne 'Y') {
        exit 0
    }
}

# Check current disk usage
Write-Host ""
Write-Host "[3/5] Checking current disk space..." -ForegroundColor Yellow

# Check E: drive space
if (Test-Path "E:\") {
    $eDrive = Get-PSDrive E
    $freeSpaceGB = [math]::Round($eDrive.Free / 1GB, 2)
    Write-Host "  E: drive free space: $freeSpaceGB GB" -ForegroundColor White
    
    if ($freeSpaceGB -lt 20) {
        Write-Host "  [WARNING] Less than 20GB free. Recommended: 20GB+" -ForegroundColor Yellow
        $response = Read-Host "Continue anyway? (y/N)"
        if ($response -ne 'y' -and $response -ne 'Y') {
            exit 0
        }
    }
} else {
    Write-Host "  [ERROR] E: drive not found!" -ForegroundColor Red
    exit 1
}

# Shutdown Docker
Write-Host ""
Write-Host "[4/5] Preparing to move Docker data..." -ForegroundColor Yellow
Write-Host "  [INFO] This will:" -ForegroundColor Yellow
Write-Host "    - Stop all running containers" -ForegroundColor White
Write-Host "    - Shutdown Docker Desktop" -ForegroundColor White
Write-Host "    - Move all Docker data to E:\DockerData" -ForegroundColor White
Write-Host "    - This may take 10-20 minutes" -ForegroundColor White
Write-Host ""

$response = Read-Host "Continue? (y/N)"
if ($response -ne 'y' -and $response -ne 'Y') {
    Write-Host "Operation cancelled." -ForegroundColor Yellow
    exit 0
}

# Create Docker data directory on E drive
$dockerDataDir = "E:\DockerData"
if (-not (Test-Path $dockerDataDir)) {
    Write-Host "  [WORKING] Creating directory: $dockerDataDir" -ForegroundColor Cyan
    New-Item -Path $dockerDataDir -ItemType Directory -Force | Out-Null
    Write-Host "  [OK] Directory created" -ForegroundColor Green
} else {
    Write-Host "  [OK] Directory already exists: $dockerDataDir" -ForegroundColor Green
}

# Shutdown Docker Desktop and WSL
Write-Host ""
Write-Host "  [WORKING] Stopping Docker Desktop..." -ForegroundColor Cyan
Write-Host "  (Please wait, this may take a minute)" -ForegroundColor Gray

# Try to stop Docker Desktop gracefully
try {
    Stop-Process -Name "Docker Desktop" -Force -ErrorAction SilentlyContinue
    Stop-Process -Name "com.docker.backend" -Force -ErrorAction SilentlyContinue
    Stop-Process -Name "com.docker.proxy" -Force -ErrorAction SilentlyContinue
} catch {
    # Ignore errors if processes not found
}

Start-Sleep -Seconds 5

# Shutdown WSL
Write-Host "  [WORKING] Shutting down WSL..." -ForegroundColor Cyan
wsl --shutdown
Start-Sleep -Seconds 10

Write-Host "  [OK] Docker stopped" -ForegroundColor Green

# Export Docker distributions
Write-Host ""
Write-Host "[5/5] Moving Docker data to E: drive..." -ForegroundColor Yellow

# Export docker-desktop
if ($dockerDesktop) {
    Write-Host "  [WORKING] Exporting docker-desktop distribution..." -ForegroundColor Cyan
    Write-Host "    (This may take 5-10 minutes)" -ForegroundColor Gray
    
    wsl --export docker-desktop "$dockerDataDir\docker-desktop.tar"
    
    if (Test-Path "$dockerDataDir\docker-desktop.tar") {
        $size = [math]::Round((Get-Item "$dockerDataDir\docker-desktop.tar").Length / 1GB, 2)
        Write-Host "  [OK] Exported docker-desktop ($size GB)" -ForegroundColor Green
    } else {
        Write-Host "  [ERROR] Failed to export docker-desktop" -ForegroundColor Red
        exit 1
    }
}

# Export docker-desktop-data
if ($dockerDesktopData) {
    Write-Host "  [WORKING] Exporting docker-desktop-data distribution..." -ForegroundColor Cyan
    Write-Host "    (This may take 5-10 minutes)" -ForegroundColor Gray
    
    wsl --export docker-desktop-data "$dockerDataDir\docker-desktop-data.tar"
    
    if (Test-Path "$dockerDataDir\docker-desktop-data.tar") {
        $size = [math]::Round((Get-Item "$dockerDataDir\docker-desktop-data.tar").Length / 1GB, 2)
        Write-Host "  [OK] Exported docker-desktop-data ($size GB)" -ForegroundColor Green
    } else {
        Write-Host "  [ERROR] Failed to export docker-desktop-data" -ForegroundColor Red
        exit 1
    }
}

# Unregister original distributions
Write-Host ""
Write-Host "  [WORKING] Removing original distributions from C: drive..." -ForegroundColor Cyan

if ($dockerDesktop) {
    wsl --unregister docker-desktop
    Write-Host "  [OK] Unregistered docker-desktop" -ForegroundColor Green
}

if ($dockerDesktopData) {
    wsl --unregister docker-desktop-data
    Write-Host "  [OK] Unregistered docker-desktop-data" -ForegroundColor Green
}

# Import to E drive
Write-Host ""
Write-Host "  [WORKING] Importing distributions to E: drive..." -ForegroundColor Cyan

if ($dockerDesktop -and (Test-Path "$dockerDataDir\docker-desktop.tar")) {
    Write-Host "  [WORKING] Importing docker-desktop..." -ForegroundColor Cyan
    wsl --import docker-desktop "$dockerDataDir\docker-desktop" "$dockerDataDir\docker-desktop.tar" --version 2
    Write-Host "  [OK] Imported docker-desktop to E:\DockerData\docker-desktop" -ForegroundColor Green
}

if ($dockerDesktopData -and (Test-Path "$dockerDataDir\docker-desktop-data.tar")) {
    Write-Host "  [WORKING] Importing docker-desktop-data..." -ForegroundColor Cyan
    wsl --import docker-desktop-data "$dockerDataDir\docker-desktop-data" "$dockerDataDir\docker-desktop-data.tar" --version 2
    Write-Host "  [OK] Imported docker-desktop-data to E:\DockerData\docker-desktop-data" -ForegroundColor Green
}

# Cleanup tar files
Write-Host ""
Write-Host "  [WORKING] Cleaning up temporary files..." -ForegroundColor Cyan

$keepTars = $false
if (Test-Path "$dockerDataDir\docker-desktop.tar") {
    $size = [math]::Round((Get-Item "$dockerDataDir\docker-desktop.tar").Length / 1GB, 2)
    Write-Host "  Found: docker-desktop.tar ($size GB)" -ForegroundColor White
    $response = Read-Host "Keep backup tar files? (y/N) [Recommended: No to save space]"
    if ($response -eq 'y' -or $response -eq 'Y') {
        $keepTars = $true
    }
}

if (-not $keepTars) {
    if (Test-Path "$dockerDataDir\docker-desktop.tar") {
        Remove-Item "$dockerDataDir\docker-desktop.tar" -Force
        Write-Host "  [OK] Removed docker-desktop.tar" -ForegroundColor Green
    }
    if (Test-Path "$dockerDataDir\docker-desktop-data.tar") {
        Remove-Item "$dockerDataDir\docker-desktop-data.tar" -Force
        Write-Host "  [OK] Removed docker-desktop-data.tar" -ForegroundColor Green
    }
} else {
    Write-Host "  [INFO] Backup tar files kept in: $dockerDataDir" -ForegroundColor Cyan
}

# Verify new location
Write-Host ""
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host "   Verifying new location..." -ForegroundColor Yellow
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host ""

$wslListNew = wsl -l -v
Write-Host $wslListNew -ForegroundColor White

# Check disk space saved
Write-Host ""
Write-Host "Disk space on E: drive:" -ForegroundColor Yellow
$dockerDataSize = Get-ChildItem -Path $dockerDataDir -Recurse -ErrorAction SilentlyContinue | 
                  Measure-Object -Property Length -Sum
if ($dockerDataSize.Sum) {
    $sizeGB = [math]::Round($dockerDataSize.Sum / 1GB, 2)
    Write-Host "  Docker data: $sizeGB GB" -ForegroundColor White
}

Write-Host ""
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host "   Migration Complete!" -ForegroundColor Green
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Docker data locations:" -ForegroundColor Yellow
Write-Host "  Old: C:\Users\<YourUser>\AppData\Local\Docker\wsl" -ForegroundColor Gray
Write-Host "  New: E:\DockerData\" -ForegroundColor Green
Write-Host ""

Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "  1. Start Docker Desktop from Start Menu" -ForegroundColor White
Write-Host "  2. Wait for Docker to initialize (1-2 minutes)" -ForegroundColor White
Write-Host "  3. Verify with: docker ps" -ForegroundColor White
Write-Host "  4. Test CabEase: cd E:\CabEase ; docker-compose up -d" -ForegroundColor White
Write-Host ""

Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host ""

$response = Read-Host "Start Docker Desktop now? (Y/n)"
if ($response -ne 'n' -and $response -ne 'N') {
    Write-Host "  [WORKING] Starting Docker Desktop..." -ForegroundColor Cyan
    
    # Try to start Docker Desktop
    $dockerPath = "C:\Program Files\Docker\Docker\Docker Desktop.exe"
    if (Test-Path $dockerPath) {
        Start-Process $dockerPath
        Write-Host "  [OK] Docker Desktop starting..." -ForegroundColor Green
        Write-Host "  [INFO] Please wait 1-2 minutes for Docker to fully start" -ForegroundColor Cyan
    } else {
        Write-Host "  [WARNING] Docker Desktop not found at default location" -ForegroundColor Yellow
        Write-Host "  Please start Docker Desktop manually from Start Menu" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "All done! Your Docker is now running from E: drive!" -ForegroundColor Green
Write-Host ""
