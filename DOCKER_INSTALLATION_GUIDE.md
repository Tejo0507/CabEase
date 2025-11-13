# Docker Desktop Installation Guide - E Drive Setup

## Overview
This guide will walk you through downloading and installing Docker Desktop on Windows with custom installation to E: drive.

---

## Step 1: Download Docker Desktop

### Download Link
**Official Docker Desktop for Windows**: https://www.docker.com/products/docker-desktop/

### Direct Download
1. Open your browser
2. Navigate to: https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe
3. Or visit: https://www.docker.com/products/docker-desktop/ and click **"Download for Windows"**

The installer is approximately **500-600 MB**.

---

## Step 2: System Requirements

### Before Installing, Verify:

**Minimum Requirements:**
- Windows 10 64-bit: Pro, Enterprise, or Education (Build 19041 or higher)
- OR Windows 11 64-bit
- Hardware virtualization enabled in BIOS
- At least 4GB RAM (8GB+ recommended)
- At least 10GB free disk space on E: drive

**Check Windows Version:**
```powershell
# Run this in PowerShell
winver
```

**Check if Virtualization is Enabled:**
```powershell
# Run this in PowerShell
Get-ComputerInfo | Select-Object HyperVisorPresent, HyperVRequirementVirtualizationFirmwareEnabled
```

If virtualization is **not enabled**, you'll need to:
1. Restart computer
2. Enter BIOS/UEFI (usually F2, F10, F12, or Del during boot)
3. Find and enable "Intel VT-x" or "AMD-V"
4. Save and exit

---

## Step 3: Install Docker Desktop on E Drive

### Installation Steps

1. **Run the Installer**
   - Locate the downloaded file: `Docker Desktop Installer.exe`
   - **Right-click** → **Run as Administrator**

2. **Installation Options**
   - ✅ Check: **"Use WSL 2 instead of Hyper-V"** (Recommended)
   - ✅ Check: **"Add shortcut to desktop"** (optional)

3. **Change Installation Directory to E Drive**

   **IMPORTANT**: By default, Docker Desktop installs to `C:\Program Files\Docker`. To install on E: drive:

   **Method 1: Using Command Line (Recommended)**
   ```powershell
   # Close the installer if already open
   # Run this in PowerShell as Administrator:
   
   cd E:\
   mkdir Docker
   
   # Run installer with custom install location
   "C:\Users\YourUsername\Downloads\Docker Desktop Installer.exe" install --installation-dir="E:\Docker"
   ```

   **Method 2: Registry Modification (Advanced)**
   If the installer doesn't support custom directory, you'll need to:
   - Install to default C: location first
   - Then move Docker data directory to E: (see Step 4)

4. **Complete Installation**
   - Wait for installation to complete (5-10 minutes)
   - Click **"Close and restart"** when prompted
   - Your computer will restart

---

## Step 4: Move Docker Data to E Drive

Even if Docker Desktop installs on C:, you can move the large data files to E: drive.

### Move WSL 2 Docker Data to E Drive

1. **Open PowerShell as Administrator**

2. **Shutdown Docker Desktop**
   ```powershell
   # Stop Docker Desktop completely
   wsl --shutdown
   ```

3. **Export Docker WSL Distribution**
   ```powershell
   # Create directory for Docker data on E drive
   New-Item -Path "E:\DockerData" -ItemType Directory -Force
   
   # Export docker-desktop
   wsl --export docker-desktop "E:\DockerData\docker-desktop.tar"
   
   # Export docker-desktop-data
   wsl --export docker-desktop-data "E:\DockerData\docker-desktop-data.tar"
   ```

4. **Unregister Original WSL Distributions**
   ```powershell
   # Remove original distributions
   wsl --unregister docker-desktop
   wsl --unregister docker-desktop-data
   ```

5. **Import to E Drive**
   ```powershell
   # Import docker-desktop to E drive
   wsl --import docker-desktop "E:\DockerData\docker-desktop" "E:\DockerData\docker-desktop.tar" --version 2
   
   # Import docker-desktop-data to E drive
   wsl --import docker-desktop-data "E:\DockerData\docker-desktop-data" "E:\DockerData\docker-desktop-data.tar" --version 2
   ```

6. **Cleanup Temporary Files**
   ```powershell
   # Remove the tar files (optional, to save space)
   Remove-Item "E:\DockerData\docker-desktop.tar"
   Remove-Item "E:\DockerData\docker-desktop-data.tar"
   ```

7. **Start Docker Desktop**
   - Open Docker Desktop from Start Menu
   - Wait for it to initialize

---

## Step 5: Verify Installation

### Check Docker is Running

1. **Open PowerShell**

2. **Verify Docker Version**
   ```powershell
   docker --version
   ```
   Should output something like: `Docker version 24.0.x, build xxxxxxx`

3. **Verify Docker Compose**
   ```powershell
   docker-compose --version
   ```
   Should output: `Docker Compose version v2.x.x`

4. **Run Test Container**
   ```powershell
   docker run hello-world
   ```
   Should download and run a test container successfully.

5. **Check WSL Integration**
   ```powershell
   wsl -l -v
   ```
   Should show:
   ```
   NAME                   STATE           VERSION
   docker-desktop         Running         2
   docker-desktop-data    Running         2
   ```

---

## Step 6: Configure Docker Settings

1. **Open Docker Desktop**
   - Click Docker icon in system tray
   - Click gear icon (⚙️) for Settings

2. **Resources → Advanced**
   - **CPUs**: 2-4 (depending on your system)
   - **Memory**: 4-8 GB (adjust based on available RAM)
   - **Swap**: 1-2 GB
   - **Disk image location**: Verify it shows E: drive path

3. **Resources → File Sharing**
   - Add `E:\CabEase` to allowed paths
   - Click **"Apply & Restart"**

4. **General Settings**
   - ✅ **Start Docker Desktop when you log in** (optional)
   - ✅ **Use WSL 2 based engine**

---

## Step 7: Verify E Drive Setup

### Check Disk Space Usage

```powershell
# Check where Docker data is stored
Get-ChildItem "E:\DockerData" -Recurse | Measure-Object -Property Length -Sum

# List WSL distributions and their locations
wsl -l -v
```

### Test with CabEase Project

1. **Navigate to Project**
   ```powershell
   cd E:\CabEase
   ```

2. **Start PostgreSQL**
   ```powershell
   docker-compose up -d
   ```

3. **Verify Containers**
   ```powershell
   docker ps
   ```
   Should show:
   - `cabease-postgres`
   - `cabease-pgadmin`

4. **Check Container Storage Location**
   ```powershell
   docker info | Select-String "Docker Root Dir"
   ```
   Should point to E: drive

---

## Step 8: Common Post-Installation Tasks

### Update Docker Desktop

```powershell
# Docker Desktop will notify you of updates
# Or check manually from Settings → About
```

### Cleanup Old Docker Data

```powershell
# Remove unused images, containers, volumes
docker system prune -a --volumes

# Check disk usage
docker system df
```

### Configure Docker to Auto-Start

1. Open Docker Desktop Settings
2. General → **"Start Docker Desktop when you log in"**
3. Click **"Apply & Restart"**

---

## Troubleshooting

### Issue 1: "Docker Desktop requires Windows 10 Pro/Enterprise"

**Solution**: Enable WSL 2
```powershell
# Run as Administrator
wsl --install
wsl --set-default-version 2
```

### Issue 2: "Hardware assisted virtualization... is not enabled"

**Solution**: 
1. Restart computer
2. Enter BIOS (F2/F10/Del during boot)
3. Enable "Intel VT-x" or "AMD-V"
4. Save and restart

### Issue 3: "WSL 2 installation is incomplete"

**Solution**:
```powershell
# Update WSL
wsl --update

# Set WSL 2 as default
wsl --set-default-version 2
```

### Issue 4: Docker not starting after moving to E drive

**Solution**:
```powershell
# Reset Docker Desktop
# Settings → Troubleshoot → Reset to factory defaults

# Then repeat Step 4 to move data to E drive
```

### Issue 5: "docker: command not found"

**Solution**:
1. Close and reopen PowerShell
2. Or add Docker to PATH manually:
   - Settings → System → About → Advanced system settings
   - Environment Variables → Path
   - Add: `C:\Program Files\Docker\Docker\resources\bin`

---

## Disk Space Management

### Monitor Docker Disk Usage

```powershell
# Check Docker disk usage
docker system df

# Detailed breakdown
docker system df -v
```

### Regular Cleanup Script

Create a PowerShell script: `E:\DockerData\cleanup-docker.ps1`

```powershell
# Docker Cleanup Script
Write-Host "Cleaning up Docker resources..." -ForegroundColor Yellow

# Remove stopped containers
docker container prune -f

# Remove unused images
docker image prune -a -f

# Remove unused volumes
docker volume prune -f

# Remove unused networks
docker network prune -f

Write-Host "Cleanup complete!" -ForegroundColor Green
docker system df
```

Run it weekly:
```powershell
E:\DockerData\cleanup-docker.ps1
```

---

## Quick Reference Commands

### Essential Docker Commands

```powershell
# Start Docker Desktop (if not auto-starting)
Start-Process "C:\Program Files\Docker\Docker\Docker Desktop.exe"

# Check Docker status
docker info

# List running containers
docker ps

# List all containers (including stopped)
docker ps -a

# Stop all containers
docker stop $(docker ps -q)

# Remove all containers
docker rm $(docker ps -a -q)

# List images
docker images

# Remove unused images
docker image prune -a

# Check disk usage
docker system df

# Complete cleanup
docker system prune -a --volumes
```

### CabEase Specific Commands

```powershell
# Navigate to project
cd E:\CabEase

# Start database
docker-compose up -d

# View logs
docker-compose logs -f

# Stop database
docker-compose down

# Stop and remove volumes (reset database)
docker-compose down -v

# Rebuild and start
docker-compose up -d --build
```

---

## Next Steps

After Docker is installed and configured on E: drive:

1. ✅ **Test Installation**
   ```powershell
   cd E:\CabEase
   docker-compose up -d
   docker ps
   ```

2. ✅ **Run CabEase**
   ```powershell
   .\start-cabease-production.ps1 -UseDocker
   ```

3. ✅ **Access Services**
   - CabEase: http://localhost:8080
   - pgAdmin: http://localhost:8081

---

## Summary

| Item | Location |
|------|----------|
| **Docker Desktop Installation** | `C:\Program Files\Docker\Docker` (program files) |
| **Docker Data (WSL)** | `E:\DockerData\docker-desktop-data` |
| **Container Images** | Stored in WSL on E: drive |
| **CabEase Project** | `E:\CabEase` |
| **Database Volumes** | `E:\DockerData` (managed by Docker) |

**Total E: Drive Space Required**: ~15-20 GB (including Docker data, images, and containers)

---

## Support Resources

- **Official Docker Docs**: https://docs.docker.com/desktop/windows/install/
- **WSL 2 Setup**: https://docs.microsoft.com/en-us/windows/wsl/install
- **Docker Hub**: https://hub.docker.com/
- **Community Forum**: https://forums.docker.com/

---

**Your Docker installation is now optimized for E: drive! 🐳**

Ready to run: `cd E:\CabEase` and `docker-compose up -d`
