try {
    $r = Invoke-WebRequest -Uri 'http://localhost:8080/api/data' -UseBasicParsing -TimeoutSec 10
    Write-Output "=== /api/data ==="
    Write-Output "Status: $($r.StatusCode)"
    Write-Output $r.Content.substring(0, [Math]::Min(500, $r.Content.length))
} catch {
    Write-Output "Error: $($_.Exception.Message)"
}
Write-Output ""
try {
    $r = Invoke-WebRequest -Uri 'http://localhost:8080/logs' -UseBasicParsing -TimeoutSec 10
    Write-Output "=== /logs ==="
    Write-Output "Status: $($r.StatusCode)"
    Write-Output $r.Content.substring(0, [Math]::Min(300, $r.Content.length))
} catch {
    Write-Output "Error: $($_.Exception.Message)"
}
