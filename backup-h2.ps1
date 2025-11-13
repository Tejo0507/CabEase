<#
Backup H2 database files into a timestamped folder (non-destructive).

Usage:
  .\backup-h2.ps1

This script copies any *.mv.db and *.trace.db files from the workspace root and the data/ folder
into a timestamped backup directory under backups/h2-backups-<timestamp>.
#>

$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$backupDir = Join-Path -Path $PSScriptRoot -ChildPath "backups/h2-backups-$timestamp"

Write-Host "Creating backup folder: $backupDir"
New-Item -ItemType Directory -Path $backupDir -Force | Out-Null

$patterns = @('*.mv.db','*.h2.db','*.trace.db')
foreach ($pattern in $patterns) {
    $files = Get-ChildItem -Path $PSScriptRoot -Recurse -Include $pattern -ErrorAction SilentlyContinue
    foreach ($f in $files) {
        $dest = Join-Path -Path $backupDir -ChildPath $f.Name
        Write-Host "Copying $($f.FullName) -> $dest"
        Copy-Item -Path $f.FullName -Destination $dest -Force
    }
}

Write-Host "Backup complete. Files copied to: $backupDir"
