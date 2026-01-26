#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo "==> Android local environment check"

if ! command -v java >/dev/null 2>&1; then
  echo "Java is required (JDK 17+). Please install it and try again."
  exit 1
fi

java -version

if [[ -z "${ANDROID_HOME:-}" && -z "${ANDROID_SDK_ROOT:-}" ]]; then
  echo "ANDROID_HOME/ANDROID_SDK_ROOT is not set."
  echo "Set ANDROID_HOME (or ANDROID_SDK_ROOT) to your Android SDK path."
  exit 1
fi

SDK_PATH="${ANDROID_SDK_ROOT:-$ANDROID_HOME}"

if [[ ! -d "$SDK_PATH" ]]; then
  echo "Android SDK path does not exist: $SDK_PATH"
  exit 1
fi

if command -v sdkmanager >/dev/null 2>&1; then
  echo "==> Ensuring Android SDK packages (api 33, build-tools 33.0.2)"
  sdkmanager --install "platforms;android-33" "build-tools;33.0.2"
else
  echo "sdkmanager not found; skipping SDK package install."
  echo "Install 'cmdline-tools' and rerun if your SDK is missing packages."
fi

echo "==> Gradle availability"
if command -v gradle >/dev/null 2>&1; then
  gradle --version
else
  echo "Gradle is not installed. Install Gradle 9.1.0 (or set a compatible version)."
  echo "CI uses Gradle 9.1.0; you can download it from https://services.gradle.org/distributions/."
  exit 1
fi

echo "==> Environment ready in $ROOT_DIR"
