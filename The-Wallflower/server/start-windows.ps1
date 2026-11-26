$ErrorActionPreference = 'Stop'
$serverRoot = Join-Path $PSScriptRoot ''
$nodeCommand = Get-Command node.exe -ErrorAction SilentlyContinue
if (-not $nodeCommand) { throw 'Node.js 18 or newer is required. Install Node.js, then run this script again.' }
$portOpen = Test-NetConnection 127.0.0.1 -Port 8765 -InformationLevel Quiet
if (-not $portOpen) {
    Start-Process -FilePath $nodeCommand.Source -ArgumentList ('"' + (Join-Path $serverRoot 'server.js') + '"') -WorkingDirectory $serverRoot -WindowStyle Hidden
    Start-Sleep -Seconds 2
}
$address = Get-NetIPAddress -AddressFamily IPv4 | Where-Object { $_.IPAddress -match '^192\.168\.' -and $_.AddressState -eq 'Preferred' } | Select-Object -First 1 -ExpandProperty IPAddress
if (-not $address) { $address = Get-NetIPAddress -AddressFamily IPv4 | Where-Object { $_.IPAddress -notlike '169.254*' -and $_.IPAddress -ne '127.0.0.1' } | Select-Object -First 1 -ExpandProperty IPAddress }
if (-not $address) { throw 'No local IPv4 address found. Connect this PC to Wi-Fi or Ethernet and try again.' }
Add-Type -AssemblyName System.Windows.Forms
[void][System.Windows.Forms.MessageBox]::Show(('The Wallflower server is running. On your phone, open http://' + $address + ':8765. Keep both devices on the same Wi-Fi.'), 'The Wallflower')
