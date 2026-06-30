try {
    $r = Invoke-WebRequest -Uri 'http://localhost:8080/' -UseBasicParsing -TimeoutSec 5
    Write-Output $r.Content.substring(0, [Math]::Min(500, $r.Content.length))
} catch {
    Write-Output "Error: $($_.Exception.Message)"
}
