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

## Container workflow (no local SDK required)
1. Build the Android container image:
   ```bash
   ./scripts/container-setup.sh
   ```
2. Optional: open a shell inside the container:
   ```bash
   ./scripts/container-shell.sh
   ```
3. Run checks and builds inside the container:
   ```bash
   ./scripts/container-test.sh
   ./scripts/container-build-apk.sh
   ./scripts/container-ci.sh
   ```

## VS Code dev container
1. Open the `app-android` folder in VS Code.
2. Run **Dev Containers: Reopen in Container**.
   - The configuration lives at `.devcontainer/devcontainer.json` and uses the Dockerfile in `container/`.

## Build an APK locally
```bash
gradle :app:assembleDebug
```
The debug APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## Notes
- CI uses Gradle 9.1.0 and Android SDK API 33 / build-tools 33.0.2.  
- If you need to update SDK packages, re-run the setup script (it uses `sdkmanager` when available).
