param(
  [switch]$SkipInstall
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $PSScriptRoot
$webPath = Join-Path $repoRoot "app-web"

if (-not (Test-Path $webPath)) {
  throw "Could not find app-web at $webPath"
}

Push-Location $webPath
try {
  if (-not $SkipInstall) {
    npm install
  }
  npm run dev
}
finally {
  Pop-Location
}
