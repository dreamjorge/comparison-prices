#!/bin/bash
# Script to run the web app in development mode on Linux/Mac

set -e

SKIP_INSTALL=false

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --skip-install)
            SKIP_INSTALL=true
            shift
            ;;
        *)
            echo "Unknown option: $1"
            echo "Usage: $0 [--skip-install]"
            exit 1
            ;;
    esac
done

echo "🚀 Starting Comparison Prices Web App"
echo ""

# Navigate to the app-web directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/.."

# Install dependencies if needed
if [ "$SKIP_INSTALL" = false ]; then
    echo "📦 Installing dependencies..."
    npm install
    echo "✅ Dependencies installed"
    echo ""
fi

# Start the dev server
echo "🌐 Starting development server..."
echo ""
npm run dev
