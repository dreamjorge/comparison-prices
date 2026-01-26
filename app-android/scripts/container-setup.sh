#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
IMAGE_NAME="${IMAGE_NAME:-comparison-prices-android}"

echo "==> Building Android container image: ${IMAGE_NAME}"
docker build -f "$ROOT_DIR/container/Dockerfile" -t "$IMAGE_NAME" "$ROOT_DIR"
