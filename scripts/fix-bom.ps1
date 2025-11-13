# Fix BOM issues in Java files
$files = Get-ChildItem -Path "E:\CabEase\src" -Filter "*.java" -Recurse

Write-Host "Found $($files.Count) Java files to check for BOM..."

foreach ($file in $files) {
    try {
        # Read file content as bytes
        $bytes = [System.IO.File]::ReadAllBytes($file.FullName)
        
        # Check for UTF-8 BOM (EF BB BF)
        if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
            Write-Host "Removing BOM from: $($file.FullName)"
            
            # Remove BOM (skip first 3 bytes)
            $content = [System.IO.File]::ReadAllText($file.FullName, [System.Text.UTF8Encoding]::new($true))
            
            # Write back without BOM
            $utf8NoBom = New-Object System.Text.UTF8Encoding $false
            [System.IO.File]::WriteAllText($file.FullName, $content, $utf8NoBom)
        }
    }
    catch {
        Write-Host "Error processing $($file.FullName): $_" -ForegroundColor Red
    }
}

Write-Host "BOM fix complete!"
