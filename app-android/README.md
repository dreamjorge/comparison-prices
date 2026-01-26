# Android app development

## Local setup (matches CI)
1. Ensure JDK 17+ is installed.
2. Set `ANDROID_HOME` or `ANDROID_SDK_ROOT` to your Android SDK path.
3. Run the setup helper:
   ```bash
   ./scripts/setup-local-android.sh
   ```

## Run CI steps locally
```bash
./scripts/run-ci-local.sh
```

## Notes
- CI uses Gradle 9.1.0 and Android SDK API 36 / build-tools 36.0.0.  
- If you need to update SDK packages, re-run the setup script (it uses `sdkmanager` when available).
