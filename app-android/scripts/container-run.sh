#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
IMAGE_NAME="${IMAGE_NAME:-comparison-prices-android}"

docker run --rm -it \
  -v "$ROOT_DIR":/workspace/app-android \
  -w /workspace/app-android \
  -e ANDROID_SDK_ROOT=/opt/android-sdk \
  -e ANDROID_HOME=/opt/android-sdk \
  "$IMAGE_NAME" "$@"
