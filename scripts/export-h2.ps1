<#
Export H2 DB to SQL using H2 Script tool.

Usage:
  .\export-h2.ps1 -H2Jar "lib\h2-2.2.220.jar" -DbUrl "jdbc:h2:file:./data/cabease" -User SA -Password ""
#>

param(
    [Parameter(Mandatory=$true)] [string] $H2Jar,
    [Parameter(Mandatory=$true)] [string] $DbUrl,
    [Parameter(Mandatory=$true)] [string] $User,
    [Parameter(Mandatory=$false)] [string] $Password = "",
    [Parameter(Mandatory=$false)] [string] $Output = "dump.sql"
)

Write-Host "Exporting H2 DB ($DbUrl) to $Output using $H2Jar"
java -cp $H2Jar org.h2.tools.Script -url $DbUrl -user $User -password $Password -script $Output
Write-Host "Export finished: $Output"
