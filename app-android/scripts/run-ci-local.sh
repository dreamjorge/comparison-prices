#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

if ! command -v gradle >/dev/null 2>&1; then
  echo "Gradle is required to run CI locally."
  echo "Install Gradle 9.1.0 or ensure it is on your PATH."
  exit 1
fi

echo "==> Running Android CI steps locally"
cd "$ROOT_DIR"

gradle :app:lint
gradle :app:test
gradle :app:assembleDebug
