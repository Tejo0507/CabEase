# Kill lingering Docker processes and restart
# Run as Administrator

Write-Host "Killing lingering Docker processes..." -ForegroundColor Yellow

# Kill specific PID
taskkill /F /PID 39416 2>&1 | Out-Null

# Kill all Docker processes
Get-Process | Where-Object {$_.ProcessName -like "*docker*"} | ForEach-Object {
    Write-Host "  Killing: $($_.ProcessName) (PID: $($_.Id))" -ForegroundColor Gray
    taskkill /F /PID $_.Id 2>&1 | Out-Null
}

Write-Host "All Docker processes stopped" -ForegroundColor Green
Start-Sleep -Seconds 3

# Shutdown WSL
Write-Host "Shutting down WSL..." -ForegroundColor Yellow
wsl --shutdown
Start-Sleep -Seconds 5

Write-Host "Starting Docker Desktop..." -ForegroundColor Cyan
Start-Process "C:\Program Files\Docker\Docker\Docker Desktop.exe"

Write-Host "`nDocker Desktop is starting!" -ForegroundColor Green
Write-Host "Wait 1-2 minutes for full initialization..." -ForegroundColor Yellow
Write-Host "Check the Docker icon in system tray" -ForegroundColor Gray
