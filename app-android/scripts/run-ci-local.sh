#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
WRAPPER_JAR="$ROOT_DIR/gradle/wrapper/gradle-wrapper.jar"
GRADLE_CMD=""

if [[ -f "$ROOT_DIR/gradlew" && -f "$WRAPPER_JAR" ]]; then
  GRADLE_CMD=(bash "$ROOT_DIR/gradlew")
elif command -v gradle >/dev/null 2>&1; then
  GRADLE_CMD=(gradle)
else
  echo "Gradle is required to run CI locally."
  echo "Install Gradle 9.1.0 or generate the wrapper jar locally."
  exit 1
fi

echo "==> Running Android CI steps locally"
cd "$ROOT_DIR"

if [[ -z "${ANDROID_HOME:-}" && -z "${ANDROID_SDK_ROOT:-}" ]]; then
  echo "ANDROID_HOME/ANDROID_SDK_ROOT is not set."
  echo "Set ANDROID_HOME (or ANDROID_SDK_ROOT) to your Android SDK path."
  exit 1
fi

"${GRADLE_CMD[@]}" :app:lint
"${GRADLE_CMD[@]}" :app:test
"${GRADLE_CMD[@]}" :app:assembleDebug
