try {
    $client = New-Object System.Net.Sockets.TcpClient
    $result = $client.BeginConnect("127.0.0.1", 8080, $null, $null)
    $wait = $result.AsyncWaitHandle.WaitOne(3000, $false)
    if ($wait) {
        $client.EndConnect($result)
        Write-Output "Connected to port 8080!"
        
        $stream = $client.GetStream()
        $writer = New-Object System.IO.StreamWriter($stream)
        $writer.WriteLine("GET / HTTP/1.1")
        $writer.WriteLine("Host: localhost:8080")
        $writer.WriteLine("Connection: close")
        $writer.WriteLine()
        $writer.Flush()
        
        $reader = New-Object System.IO.StreamReader($stream)
        $response = $reader.ReadToEnd()
        Write-Output "Response length: $($response.Length)"
        if ($response.Length -gt 0) {
            Write-Output $response.substring(0, [Math]::Min(300, $response.Length))
        }
        $reader.Close()
    } else {
        Write-Output "Connection timed out to port 8080"
    }
    $client.Close()
} catch {
    Write-Output "Error: $($_.Exception.Message)"
}
