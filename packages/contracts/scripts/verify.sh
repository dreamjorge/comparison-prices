#!/bin/bash
set -e

# Navigate to the contracts package root
cd "$(dirname "$0")/.."

echo "Verifying contract synchronization..."

# Run the generation script
npm run generate

# Check if src/generated.ts has changed
if [ -n "$(git status --porcelain src/generated.ts)" ]; then
  echo "❌ Error: src/generated.ts is out of sync with openapi.json"
  echo "Please run 'npm run generate' in packages/contracts and commit the changes."
  git diff src/generated.ts
  exit 1
else
  echo "✅ Success: Contracts are in sync."
fi
