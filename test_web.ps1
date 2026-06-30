try {
    $r = Invoke-WebRequest -Uri 'http://localhost:8080/' -UseBasicParsing -TimeoutSec 10
    Write-Output "Status: $($r.StatusCode)"
    Write-Output "Content length: $($r.RawContentLength)"
    $content = $r.Content
    if ($content.Length -gt 0) {
        Write-Output "First 300 chars:"
        Write-Output $content.substring(0, [Math]::Min(300, $content.Length))
    }
} catch {
    Write-Output "Error: $($_.Exception.Message)"
}
