# Agent instructions for Android app development

## Local environment
- Use JDK 17+ and set `ANDROID_HOME` or `ANDROID_SDK_ROOT`.
- Run `./scripts/setup-local-android.sh` to align your environment with CI.

## Required checks before delivery
- Run the same tasks as CI from `app-android/`:
  - `./scripts/run-ci-local.sh`

## Notes
- CI uses Gradle 9.1.0 and Android SDK API 35 / build-tools 35.0.0.
- Prefer updating this file when CI requirements change so agents stay aligned.
