# Build & Signing Instructions

## ⚠️ aarch64 / ARM64 Linux — Local Build Limitation

**Problem:** Google's Android build-tools ship AAPT2 as an **x86_64-only binary**. On ARM64 Linux hosts (e.g. Rockchip RK3588, Raspberry Pi, Apple M-series via Linux VM), `./gradlew :app:test` and any variant that compiles resources will fail with:

```
AAPT2 Daemon startup failed
Syntax error: ")" unexpected
```

**Root cause:** The `aapt2` binary inside `build-tools/<version>/` and the one cached under `~/.gradle/caches/*/aapt2-*-linux/aapt2` are ELF x86_64 executables. `binfmt_misc` (needed for QEMU user-mode to intercept them) is typically unavailable inside containers.

**Options to unblock local builds on aarch64:**

| Option | Effort | Notes |
|--------|--------|-------|
| Use community aarch64 AAPT2 | Low | Download pre-built from [`lzhiyong/android-sdk-tools`](https://github.com/lzhiyong/android-sdk-tools/releases) and replace `build-tools/<ver>/aapt2` |
| Docker `--platform linux/amd64` | Low | Requires Docker with QEMU binfmt registered at host level (Docker Desktop does this automatically) |
| Separate pure-Kotlin Gradle module | Medium | Move business-logic tests out of the Android module into a `:core` JVM module — no AAPT2 needed |
| CI only (GitHub Actions x86_64) | Zero | Run `./scripts/run-ci-local.sh` via the devcontainer job; unit tests pass on the CI runner |

**Recommended short-term:** rely on CI for Android builds/tests. For local iteration, use the CI devcontainer:

```bash
# From repo root — requires Docker
docker run --rm -v "$PWD/app-android:/workspace" \
  --platform linux/amd64 \
  $(cat app-android/.devcontainer/devcontainer.json | grep '"image"' | sed 's/.*: "//;s/".*//') \
  bash -c "cd /workspace && ./scripts/run-ci-local.sh"
```

---

Instructions to generate the production-ready Android App Bundle (AAB).

## 1. Keystore Configuration
Create a `keystore.properties` file in the root directory (DO NOT COMMIT THIS):
```properties
storePassword=your_password
keyPassword=your_password
keyAlias=your_alias
storeFile=path/to/your/upload-keystore.jks
```

## 2. Generate Release Bundle
Run the following command in the terminal:
```bash
./gradlew bundleRelease
```

## 3. Locate the Artifact
The resulting `.aab` file will be located at:
`app/build/outputs/bundle/release/app-release.aab`

## 4. Testing the Release Build
Use `bundletool` to install the AAB on a test device:
```bash
bundletool build-apks --bundle=app-release.aab --output=app.apks --ks=keystore.jks
bundletool install-apks --apks=app.apks
```
