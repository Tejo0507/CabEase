$sourceDir = "E:\CabEase\src"
$javaFiles = Get-ChildItem -Path $sourceDir -Filter "*.java" -Recurse

$totalFiles = $javaFiles.Count
$processedFiles = 0

Write-Host "Found $totalFiles Java files to process..." -ForegroundColor Cyan

foreach ($file in $javaFiles) {
    $processedFiles++
    Write-Host "[$processedFiles/$totalFiles] Processing: $($file.FullName)" -ForegroundColor Yellow
    
    try {
        $lines = Get-Content $file.FullName -Encoding UTF8
        $cleanedLines = @()
        $inBlockComment = $false
        
        foreach ($line in $lines) {
            $cleanLine = $line
            
            if ($inBlockComment) {
                if ($cleanLine -match '\*/') {
                    $inBlockComment = $false
                    $cleanLine = $cleanLine -replace '^.*?\*/', ''
                } else {
                    continue
                }
            }
            
            if ($cleanLine -match '/\*') {
                if ($cleanLine -match '/\*.*?\*/') {
                    $cleanLine = $cleanLine -replace '/\*.*?\*/', ''
                } else {
                    $inBlockComment = $true
                    $cleanLine = $cleanLine -replace '/\*.*$', ''
                }
            }
            
            $cleanLine = $cleanLine -replace '//.*$', ''
            
            if ($cleanLine.Trim() -ne '') {
                $cleanedLines += $cleanLine.TrimEnd()
            }
        }
        
        $content = $cleanedLines -join "`r`n"
        Set-Content -Path $file.FullName -Value $content -Encoding UTF8
        
        Write-Host "  Cleaned $($file.Name)" -ForegroundColor Green
    }
    catch {
        Write-Host "  Error processing $($file.Name): $_" -ForegroundColor Red
    }
}

Write-Host "`nCompleted! Processed $processedFiles files." -ForegroundColor Green
