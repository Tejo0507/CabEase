<#
Auto-export H2 DB (non-destructive).

What it does:
- Runs `backup-h2.ps1` to create a safe copy of H2 files.
- Locates an H2 jar (lib/ or local maven_repo) to run the H2 Script tool.
- Gathers password candidates from environment variables and local config files.
- Attempts to export H2 using each candidate (including empty) until success.

Usage:
  .\auto-export-h2.ps1

Notes:
- This script will NOT attempt brute-force cracking. It only tries plausible candidates found in the repo or environment.
- If export fails, the H2 DB is likely protected by an unknown password; restore from backups or locate credentials.
#>

Set-StrictMode -Version Latest

function Find-H2Jar {
    # Prefer lib/ first
    $libPath = Join-Path $PSScriptRoot 'lib'
    if (Test-Path $libPath) {
        $found = Get-ChildItem -Path $libPath -Filter 'h2-*.jar' -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($found) { return $found.FullName }
    }
    # Fallback to local maven repo
    $mavenCandidate = Join-Path $PSScriptRoot 'maven_repo\com\h2database\h2'
    if (Test-Path $mavenCandidate) {
        $jar = Get-ChildItem -Path $mavenCandidate -Recurse -Filter 'h2-*.jar' -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($jar) { return $jar.FullName }
    }
    return $null
}

function Get-PasswordCandidates {
    $candidates = @()
    # Environment vars
    $envVars = @('H2_PASSWORD','SPRING_DATASOURCE_PASSWORD','DB_PASSWORD','SA_PASSWORD','SPRING_DATASOURCE_USERNAME')
    foreach ($n in $envVars) {
        $v = [Environment]::GetEnvironmentVariable($n)
        if ($v -ne $null -and $v -ne '') { $candidates += $v }
    }

    # application.properties
    $appProps = Join-Path $PSScriptRoot 'src\main\resources\application.properties'
    if (Test-Path $appProps) {
        $lines = Get-Content $appProps -ErrorAction SilentlyContinue
        foreach ($l in $lines) {
            if ($l -match 'spring\.datasource\.password\s*=\s*(.*)') {
                $val = $matches[1].Trim()
                if ($val -ne '') { $candidates += $val }
            }
            if ($l -match 'spring\.datasource\.username\s*=\s*(.*)') {
                $val = $matches[1].Trim()
                if ($val -ne '') { $candidates += $val }
            }
        }
    }

    # DatabaseService.java DB_PASSWORD constant
    $dbSvc = Join-Path $PSScriptRoot 'src\com\cabease\utils\DatabaseService.java'
    if (Test-Path $dbSvc) {
        $svcLines = Get-Content $dbSvc -ErrorAction SilentlyContinue
        foreach ($l in $svcLines) {
            if ($l -match 'DB_PASSWORD\s*=\s*"(.*)"') {
                $val = $matches[1]
                if ($val -ne '') { $candidates += $val }
            }
        }
    }

    # Always try empty string (common for dev H2)
    $candidates += ''

    # Deduplicate preserve order
    $seen = @{}
    $out = @()
    foreach ($c in $candidates) {
        if (-not $seen.ContainsKey($c)) { $seen[$c] = $true; $out += $c }
    }
    return $out
}

Write-Host "[auto-export-h2] Running backup-h2.ps1 first (non-destructive)..."
& "$PSScriptRoot\backup-h2.ps1"

$h2Jar = Find-H2Jar
if (-not $h2Jar) {
    Write-Warning "No H2 jar found in lib/ or maven_repo. Please place an H2 jar in lib/ or ensure maven_repo contains one. Aborting export attempts."
    exit 1
}

Write-Host "[auto-export-h2] Using H2 jar: $h2Jar"

$dbUrlCandidates = @('jdbc:h2:file:./data/cabease', 'jdbc:h2:./cabease_db', 'jdbc:h2:file:./cabease', 'jdbc:h2:./data/cabease')
$dbUrl = $null
foreach ($u in $dbUrlCandidates) { if (Test-Path (Join-Path $PSScriptRoot ($u -replace 'jdbc:h2:(file:)?\.?/',''))) { $dbUrl = $u; break } }
if (-not $dbUrl) { $dbUrl = 'jdbc:h2:file:./data/cabease' }

Write-Host "[auto-export-h2] Using DB URL: $dbUrl"

$passwords = Get-PasswordCandidates
Write-Host "[auto-export-h2] Password candidates to try:"
$i = 0
foreach ($p in $passwords) { $i++; $display = if ($p -eq '') { '<empty>' } else { '[hidden]' }; Write-Host "  $i) $display" }

$attempt = 0
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
foreach ($pw in $passwords) {
    $attempt++
    $outFile = Join-Path $PSScriptRoot "dump_h2_${timestamp}_$attempt.sql"
    Write-Host "[auto-export-h2] Attempt $attempt: exporting to $outFile"
    $args = @('-cp', '"' + $h2Jar + '"', 'org.h2.tools.Script', '-url', '"' + $dbUrl + '"', '-user', 'SA', '-password', '"' + $pw + '"', '-script', '"' + $outFile + '"')
    # Build a single command to avoid quoting headaches
    $cmd = "java -cp `"$h2Jar`" org.h2.tools.Script -url `"$dbUrl`" -user SA -password `"$pw`" -script `"$outFile`""
    Write-Host "[auto-export-h2] Running: $cmd"
    $proc = Start-Process -FilePath java -ArgumentList "-cp`,"$h2Jar`",org.h2.tools.Script,-url,$dbUrl,-user,SA,-password,$pw,-script,$outFile" -NoNewWindow -Wait -PassThru -ErrorAction SilentlyContinue
    if ($proc.ExitCode -eq 0) {
        Write-Host "[auto-export-h2] Export successful -> $outFile"
        exit 0
    } else {
        Write-Warning "[auto-export-h2] Attempt $attempt failed (exit code $($proc.ExitCode))."
        # Capture stderr by re-running and redirecting output to temp file to show last error
        $logFile = Join-Path $PSScriptRoot "h2_export_attempt_${timestamp}_$attempt.log"
        & java -cp "$h2Jar" org.h2.tools.Script -url "$dbUrl" -user SA -password "$pw" -script "$outFile" 2> "$logFile" | Out-Null
        if (Test-Path $logFile) {
            $err = Get-Content $logFile -Tail 20 -ErrorAction SilentlyContinue
            if ($err) { Write-Host "[auto-export-h2] Last 20 lines of stderr for attempt $attempt:"; $err | ForEach-Object { Write-Host $_ } }
        }
    }
}

Write-Error "[auto-export-h2] All attempts failed. The H2 DB is likely password-protected with an unknown password. See H2_TO_POSTGRES_PLAYBOOK.md for next steps."
exit 2
