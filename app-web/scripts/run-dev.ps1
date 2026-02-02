#!/usr/bin/env pwsh
# Script to run the web app in development mode on Windows

param(
    [switch]$SkipInstall
)

$ErrorActionPreference = "Stop"

Write-Host "🚀 Starting Comparison Prices Web App" -ForegroundColor Cyan
Write-Host ""

# Navigate to the app-web directory
$scriptDir = Split-Path -Parent $PSCommandPath
Set-Location (Join-Path $scriptDir "..")

# Install dependencies if needed
if (-not $SkipInstall) {
    Write-Host "📦 Installing dependencies..." -ForegroundColor Yellow
    npm install
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Failed to install dependencies" -ForegroundColor Red
        exit 1
    }
    Write-Host "✅ Dependencies installed" -ForegroundColor Green
    Write-Host ""
}

# Start the dev server
Write-Host "🌐 Starting development server..." -ForegroundColor Yellow
Write-Host ""
npm run dev
