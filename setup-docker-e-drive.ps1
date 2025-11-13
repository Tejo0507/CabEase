# Docker Desktop Setup Automation Script
# This script helps download and configure Docker Desktop for E: drive

param(
    [switch]$DownloadOnly = $false,
    [switch]$MoveDataOnly = $false
)

Write-Host "═══════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "   Docker Desktop Setup for E: Drive" -ForegroundColor Cyan
Write-Host "═══════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

# Check if running as Administrator
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
if (-not $isAdmin) {
    Write-Host "⚠ This script requires Administrator privileges!" -ForegroundColor Yellow
    Write-Host "  Please run PowerShell as Administrator and try again." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "  Right-click PowerShell → Run as Administrator" -ForegroundColor White
    exit 1
}

# Function to check system requirements
function Test-SystemRequirements {
    Write-Host "[1/7] Checking system requirements..." -ForegroundColor Yellow
    
    # Check Windows version
    $osInfo = Get-CimInstance Win32_OperatingSystem
    $buildNumber = [int]$osInfo.BuildNumber
    
    Write-Host "  OS: $($osInfo.Caption)" -ForegroundColor White
    Write-Host "  Build: $buildNumber" -ForegroundColor White
    
    if ($buildNumber -lt 19041) {
        Write-Host "  ✗ Windows 10 build 19041 or higher required!" -ForegroundColor Red
        return $false
    }
    
    # Check virtualization
    $hyperV = Get-ComputerInfo -Property HyperVisorPresent, HyperVRequirementVirtualizationFirmwareEnabled
    if ($hyperV.HyperVRequirementVirtualizationFirmwareEnabled) {
        Write-Host "  ✓ Hardware virtualization: Enabled" -ForegroundColor Green
    } else {
        Write-Host "  ✗ Hardware virtualization: Disabled" -ForegroundColor Red
        Write-Host "    Please enable VT-x/AMD-V in BIOS" -ForegroundColor Yellow
        return $false
    }
    
    # Check RAM
    $ram = [math]::Round($osInfo.TotalVisibleMemorySize / 1MB, 2)
    Write-Host "  RAM: $ram GB" -ForegroundColor White
    if ($ram -lt 4) {
        Write-Host "  ⚠ At least 4GB RAM recommended" -ForegroundColor Yellow
    }
    
    # Check E: drive space
    if (Test-Path "E:\") {
        $drive = Get-PSDrive E
        $freeSpaceGB = [math]::Round($drive.Free / 1GB, 2)
        Write-Host "  E: drive free space: $freeSpaceGB GB" -ForegroundColor White
        if ($freeSpaceGB -lt 10) {
            Write-Host "  ✗ At least 10GB free space required on E: drive" -ForegroundColor Red
            return $false
        }
    } else {
        Write-Host "  ✗ E: drive not found!" -ForegroundColor Red
        return $false
    }
    
    Write-Host "  ✓ System requirements met" -ForegroundColor Green
    return $true
}

# Function to download Docker Desktop
function Get-DockerDesktop {
    Write-Host ""
    Write-Host "[2/7] Downloading Docker Desktop..." -ForegroundColor Yellow
    
    $downloadUrl = "https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe"
    $installerPath = "E:\Docker Desktop Installer.exe"
    
    if (Test-Path $installerPath) {
        Write-Host "  ℹ Installer already exists at: $installerPath" -ForegroundColor Cyan
        $response = Read-Host "  Download again? (y/N)"
        if ($response -ne 'y' -and $response -ne 'Y') {
            Write-Host "  ✓ Using existing installer" -ForegroundColor Green
            return $installerPath
        }
    }
    
    try {
        Write-Host "  ⟳ Downloading from: $downloadUrl" -ForegroundColor Cyan
        Write-Host "  ⟳ Destination: $installerPath" -ForegroundColor Cyan
        Write-Host "  ⟳ This may take 5-10 minutes (500+ MB download)..." -ForegroundColor Cyan
        
        $ProgressPreference = 'SilentlyContinue'
        Invoke-WebRequest -Uri $downloadUrl -OutFile $installerPath -UseBasicParsing
        $ProgressPreference = 'Continue'
        
        Write-Host "  ✓ Download complete!" -ForegroundColor Green
        return $installerPath
    } catch {
        Write-Host "  ✗ Download failed: $_" -ForegroundColor Red
        Write-Host ""
        Write-Host "  Please download manually from:" -ForegroundColor Yellow
        Write-Host "  https://www.docker.com/products/docker-desktop/" -ForegroundColor Cyan
        return $null
    }
}

# Function to install Docker Desktop
function Install-DockerDesktop {
    param([string]$installerPath)
    
    Write-Host ""
    Write-Host "[3/7] Installing Docker Desktop..." -ForegroundColor Yellow
    
    if (-not (Test-Path $installerPath)) {
        Write-Host "  ✗ Installer not found at: $installerPath" -ForegroundColor Red
        return $false
    }
    
    Write-Host "  ⟳ Running installer..." -ForegroundColor Cyan
    Write-Host "  ⟳ Please follow the installation wizard:" -ForegroundColor Cyan
    Write-Host "    - Use WSL 2 instead of Hyper-V (recommended)" -ForegroundColor White
    Write-Host "    - Add desktop shortcut (optional)" -ForegroundColor White
    Write-Host ""
    
    try {
        Start-Process -FilePath $installerPath -ArgumentList "install", "--quiet" -Wait
        Write-Host "  ✓ Installation complete!" -ForegroundColor Green
        Write-Host "  ℹ You may need to restart your computer" -ForegroundColor Cyan
        return $true
    } catch {
        Write-Host "  ✗ Installation failed: $_" -ForegroundColor Red
        Write-Host "  ℹ Please run the installer manually: $installerPath" -ForegroundColor Yellow
        return $false
    }
}

# Function to move Docker data to E drive
function Move-DockerDataToE {
    Write-Host ""
    Write-Host "[4/7] Moving Docker data to E: drive..." -ForegroundColor Yellow
    
    # Create Docker data directory
    $dockerDataDir = "E:\DockerData"
    if (-not (Test-Path $dockerDataDir)) {
        New-Item -Path $dockerDataDir -ItemType Directory -Force | Out-Null
        Write-Host "  ✓ Created: $dockerDataDir" -ForegroundColor Green
    }
    
    # Shutdown WSL
    Write-Host "  ⟳ Shutting down WSL..." -ForegroundColor Cyan
    wsl --shutdown
    Start-Sleep -Seconds 5
    
    # Export distributions
    Write-Host "  ⟳ Exporting docker-desktop..." -ForegroundColor Cyan
    wsl --export docker-desktop "$dockerDataDir\docker-desktop.tar"
    
    Write-Host "  ⟳ Exporting docker-desktop-data..." -ForegroundColor Cyan
    wsl --export docker-desktop-data "$dockerDataDir\docker-desktop-data.tar"
    
    # Unregister original distributions
    Write-Host "  ⟳ Unregistering original distributions..." -ForegroundColor Cyan
    wsl --unregister docker-desktop
    wsl --unregister docker-desktop-data
    
    # Import to E drive
    Write-Host "  ⟳ Importing to E: drive..." -ForegroundColor Cyan
    wsl --import docker-desktop "$dockerDataDir\docker-desktop" "$dockerDataDir\docker-desktop.tar" --version 2
    wsl --import docker-desktop-data "$dockerDataDir\docker-desktop-data" "$dockerDataDir\docker-desktop-data.tar" --version 2
    
    # Cleanup tar files
    Write-Host "  ⟳ Cleaning up temporary files..." -ForegroundColor Cyan
    Remove-Item "$dockerDataDir\docker-desktop.tar" -Force
    Remove-Item "$dockerDataDir\docker-desktop-data.tar" -Force
    
    Write-Host "  ✓ Docker data moved to E: drive!" -ForegroundColor Green
}

# Function to verify Docker installation
function Test-DockerInstallation {
    Write-Host ""
    Write-Host "[5/7] Verifying Docker installation..." -ForegroundColor Yellow
    
    # Check if Docker command exists
    try {
        $dockerVersion = docker --version 2>$null
        if ($dockerVersion) {
            Write-Host "  ✓ $dockerVersion" -ForegroundColor Green
        } else {
            Write-Host "  ✗ Docker command not found" -ForegroundColor Red
            Write-Host "  ℹ Please restart your computer and try again" -ForegroundColor Yellow
            return $false
        }
    } catch {
        Write-Host "  ⚠ Docker not in PATH yet" -ForegroundColor Yellow
        Write-Host "  ℹ Please restart your computer" -ForegroundColor Cyan
        return $false
    }
    
    # Check Docker Compose
    try {
        $composeVersion = docker-compose --version 2>$null
        if ($composeVersion) {
            Write-Host "  ✓ $composeVersion" -ForegroundColor Green
        }
    } catch {
        Write-Host "  ⚠ Docker Compose not available yet" -ForegroundColor Yellow
    }
    
    # Test with hello-world
    Write-Host "  ⟳ Running test container..." -ForegroundColor Cyan
    try {
        docker run --rm hello-world 2>$null | Out-Null
        Write-Host "  ✓ Docker is working correctly!" -ForegroundColor Green
        return $true
    } catch {
        Write-Host "  ⚠ Docker may not be running yet" -ForegroundColor Yellow
        Write-Host "  ℹ Please start Docker Desktop and try again" -ForegroundColor Cyan
        return $false
    }
}

# Function to configure Docker settings
function Set-DockerConfiguration {
    Write-Host ""
    Write-Host "[6/7] Configuring Docker settings..." -ForegroundColor Yellow
    
    $settingsFile = "$env:APPDATA\Docker\settings.json"
    
    if (Test-Path $settingsFile) {
        Write-Host "  ℹ Docker settings file found" -ForegroundColor Cyan
        Write-Host "  ℹ You can configure Docker from Docker Desktop Settings" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "  Recommended settings:" -ForegroundColor Yellow
        Write-Host "    • CPUs: 2-4" -ForegroundColor White
        Write-Host "    • Memory: 4-8 GB" -ForegroundColor White
        Write-Host "    • Enable WSL 2" -ForegroundColor White
        Write-Host "    • Add E:\CabEase to file sharing" -ForegroundColor White
    } else {
        Write-Host "  ℹ Settings file not found (Docker may not be started yet)" -ForegroundColor Cyan
    }
}

# Function to test with CabEase
function Test-CabEaseDocker {
    Write-Host ""
    Write-Host "[7/7] Testing with CabEase project..." -ForegroundColor Yellow
    
    if (-not (Test-Path "E:\CabEase\docker-compose.yml")) {
        Write-Host "  ⚠ CabEase project not found at E:\CabEase" -ForegroundColor Yellow
        return
    }
    
    $response = Read-Host "  Start CabEase PostgreSQL containers? (y/N)"
    if ($response -eq 'y' -or $response -eq 'Y') {
        Push-Location "E:\CabEase"
        
        Write-Host "  ⟳ Starting containers..." -ForegroundColor Cyan
        docker-compose up -d
        
        Write-Host ""
        Write-Host "  ⟳ Checking containers..." -ForegroundColor Cyan
        docker ps
        
        Pop-Location
        
        Write-Host ""
        Write-Host "  ✓ CabEase containers started!" -ForegroundColor Green
        Write-Host "    • PostgreSQL: localhost:5432" -ForegroundColor White
        Write-Host "    • pgAdmin: http://localhost:8081" -ForegroundColor White
    }
}

# Main execution
Write-Host ""

if (-not (Test-SystemRequirements)) {
    Write-Host ""
    Write-Host "System requirements not met. Please fix the issues and try again." -ForegroundColor Red
    exit 1
}

if (-not $MoveDataOnly) {
    $installerPath = Get-DockerDesktop
    
    if (-not $installerPath) {
        Write-Host ""
        Write-Host "Failed to download Docker Desktop." -ForegroundColor Red
        exit 1
    }
    
    if ($DownloadOnly) {
        Write-Host ""
        Write-Host "═══════════════════════════════════════════════════════" -ForegroundColor Cyan
        Write-Host "  Download Complete!" -ForegroundColor Green
        Write-Host "═══════════════════════════════════════════════════════" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "Installer location: $installerPath" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "Next steps:" -ForegroundColor Yellow
        Write-Host "  1. Double-click the installer to install Docker Desktop" -ForegroundColor White
        Write-Host "  2. Restart your computer" -ForegroundColor White
        Write-Host "  3. Run this script again to move data to E: drive" -ForegroundColor White
        exit 0
    }
    
    if (-not (Install-DockerDesktop -installerPath $installerPath)) {
        Write-Host ""
        Write-Host "Installation failed or incomplete." -ForegroundColor Red
        exit 1
    }
}

# Check if Docker is installed
try {
    docker --version | Out-Null
    $dockerInstalled = $true
} catch {
    $dockerInstalled = $false
}

if ($dockerInstalled) {
    Move-DockerDataToE
    Test-DockerInstallation
    Set-DockerConfiguration
    Test-CabEaseDocker
} else {
    Write-Host ""
    Write-Host "═══════════════════════════════════════════════════════" -ForegroundColor Cyan
    Write-Host "  Installation Complete - Restart Required" -ForegroundColor Yellow
    Write-Host "═══════════════════════════════════════════════════════" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Next steps:" -ForegroundColor Yellow
    Write-Host "  1. Restart your computer" -ForegroundColor White
    Write-Host "  2. Start Docker Desktop" -ForegroundColor White
    Write-Host "  3. Run this script again to move data to E: drive:" -ForegroundColor White
    Write-Host "     .\setup-docker-e-drive.ps1 -MoveDataOnly" -ForegroundColor Cyan
}

Write-Host ""
Write-Host "═══════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "  Setup Complete!" -ForegroundColor Green
Write-Host "═══════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""
