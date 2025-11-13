# Docker Setup - Quick Start Card

## 🚀 Fastest Way to Get Docker on E: Drive

### Option 1: Automated Script (Recommended)
```powershell
# Run PowerShell as Administrator
cd E:\CabEase
.\setup-docker-e-drive.ps1
```

### Option 2: Manual Installation

**Step 1: Download**
- Visit: https://www.docker.com/products/docker-desktop/
- Or direct: https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe
- Save to: `E:\Docker Desktop Installer.exe`

**Step 2: Install**
```powershell
# Run as Administrator
E:\"Docker Desktop Installer.exe" install
```

**Step 3: Move to E Drive** (after restart)
```powershell
# Run as Administrator
cd E:\CabEase
.\setup-docker-e-drive.ps1 -MoveDataOnly
```

---

## ✅ Quick Verification

```powershell
# Check Docker is installed
docker --version

# Check Docker is running
docker ps

# Test Docker works
docker run hello-world

# Check data location
wsl -l -v
```

---

## 🎯 Start CabEase with Docker

```powershell
# Navigate to project
cd E:\CabEase

# Start database
docker-compose up -d

# Verify containers
docker ps

# Or use automated script
.\start-cabease-production.ps1 -UseDocker
```

---

## 📊 Disk Space on E: Drive

| Component | Size |
|-----------|------|
| Docker Desktop Program | ~500 MB (on C:) |
| Docker WSL Data | ~5-10 GB |
| Container Images | ~2-5 GB |
| CabEase Volumes | ~1-2 GB |
| **Total E: Drive** | **~10-20 GB** |

---

## 🔧 Common Commands

```powershell
# Start Docker Desktop
Start-Process "C:\Program Files\Docker\Docker\Docker Desktop.exe"

# Shutdown Docker/WSL
wsl --shutdown

# Check disk usage
docker system df

# Cleanup unused data
docker system prune -a

# View container logs
docker-compose logs -f

# Stop all containers
docker stop $(docker ps -q)
```

---

## 📁 Important Locations

```
C:\Program Files\Docker\Docker\     → Docker Desktop installation
E:\DockerData\                      → Docker WSL data (moved here)
E:\CabEase\                         → Your CabEase project
```

---

## 🆘 Troubleshooting Quick Fixes

**Docker won't start**
```powershell
wsl --shutdown
# Restart Docker Desktop
```

**"Command not found"**
```powershell
# Close and reopen PowerShell
# Or restart computer
```

**Out of disk space**
```powershell
docker system prune -a --volumes
```

**Virtualization error**
- Restart computer
- Enter BIOS (F2/F10/Del)
- Enable "Intel VT-x" or "AMD-V"

---

## 📖 Documentation Files

- `DOCKER_INSTALLATION_GUIDE.md` - Complete installation guide
- `setup-docker-e-drive.ps1` - Automated setup script
- `DATABASE_SETUP_GUIDE.md` - Database setup after Docker

---

## ⚡ TL;DR

```powershell
# 1. Run as Administrator
.\setup-docker-e-drive.ps1

# 2. Restart computer

# 3. Start Docker Desktop

# 4. Test CabEase
cd E:\CabEase
docker-compose up -d

# 5. Access
# http://localhost:8080 - CabEase
# http://localhost:8081 - pgAdmin
```

---

**Need Help?** Check `DOCKER_INSTALLATION_GUIDE.md` for detailed instructions!
