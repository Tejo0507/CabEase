<#
Import sanitized SQL into PostgreSQL using psql CLI.

Usage:
  .\import-to-postgres.ps1 -Host localhost -Port 5432 -DbName cabease -User postgres -File dump_sanitized.sql
#>

param(
    [Parameter(Mandatory=$true)] [string] $Host,
    [Parameter(Mandatory=$false)] [int] $Port = 5432,
    [Parameter(Mandatory=$true)] [string] $DbName,
    [Parameter(Mandatory=$true)] [string] $User,
    [Parameter(Mandatory=$false)] [string] $File
)

if (-not $File) {
    Write-Error "Please pass -File path to the sanitized SQL file to import."
    exit 1
}

Write-Host "Importing $File into $DbName@$Host:$Port as $User"
$env:PGPASSWORD = Read-Host -AsSecureString "Postgres password" | ConvertFrom-SecureString
# ConvertFrom-SecureString stores encrypted data; prefer using environment variables or a secure vault in CI.

psql -h $Host -p $Port -U $User -d $DbName -f $File

Write-Host "Import finished (check psql output for errors)"
