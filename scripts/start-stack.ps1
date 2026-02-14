param(
  [switch]$SkipInstall
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $PSScriptRoot
$apiPath = Join-Path $repoRoot "api"
$webPath = Join-Path $repoRoot "app-web"

if (-not (Test-Path $apiPath)) {
  throw "Could not find api at $apiPath"
}

if (-not (Test-Path $webPath)) {
  throw "Could not find app-web at $webPath"
}

if (-not $SkipInstall) {
  Push-Location $apiPath
  try {
    npm install
  }
  finally {
    Pop-Location
  }

  Push-Location $webPath
  try {
    npm install
  }
  finally {
    Pop-Location
  }
}

$apiCommand = "cd /d `"$apiPath`" && npm run dev"
$webCommand = "cd /d `"$webPath`" && npm run dev"

Start-Process -FilePath "cmd.exe" -ArgumentList "/k $apiCommand"
Start-Process -FilePath "cmd.exe" -ArgumentList "/k $webCommand"

Write-Host "API starting at http://localhost:4000"
Write-Host "Web starting at http://localhost:4173"
