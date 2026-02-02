# 🛠 Project Skills & Automation

This document defines the specialized skills and automated tasks for each agent role in the **Comparison Prices (LATAM)** project.

---

## 📱 Android Core (Agent 2)
**Responsibility**: Jetpack Compose UI, Room DB, WorkManager, and Local Business Logic.

- **Build & Test**:
  - Run `./gradlew test` in `app-android/` to execute unit tests.
  - Run `./gradlew assembleDebug` to build the APK.
- **Environment**:
  - Use `app-android/scripts/setup-local-android.sh` to configure the local dev environment.
  - Use `app-android/scripts/container-ci.sh` for CI-like validation inside a container.
- **Key Files**:
  - `app-android/app/src/main/java/.../data/` (Room Entities/DAOs)
  - `app-android/app/src/main/java/.../ui/` (Compose Screens)

---

## 🌐 Web & Contracts (Agent 2/Agent 3)
**Responsibility**: React/Vite Frontend and shared DTO synchronization.

- **Contracts Sync**:
  - In `packages/contracts/`, run `npm run generate` to sync TypeScript types from `openapi.json`.
  - Ensure all API calls in `app-web/` use types from `@comparison-prices/contracts`.
- **Frontend Development**:
  - In `app-web/`, run `npm run dev` to start the Vite dev server.
  - Run `npm run build` to verify production builds.

---

## 🐍 Data & API (Agent 3)
**Responsibility**: FastAPI backend, price normalization, and data fetching.

- **API Maintenance**:
  - Implement endpoints in `api-python/`.
  - Maintain `packages/contracts/openapi.json` as the source of truth for the API.
- **Data Normalization**:
  - Scripts for cleaning brand names, units (kg/g/ml), and merchant IDs.

---

## 🦀 Matching Engine (Agent 4)
**Responsibility**: High-performance fuzzy matching for LATAM products.

- **Rust Logic**:
  - Implement Jaro-Winkler/Levenshtein algorithms in `matching-rust/`.
  - Ensure compatibility with Android via JNI/FFI if integrated locally.

---

## 🚀 QA & DevOps (Agent 6)
**Responsibility**: CI/CD, Play Store releases, and stability.

- **CI Pipeline**:
  - Check `.github/workflows/` for automated build and test triggers.
  - Ensure `app-android/scripts/run-ci-local.sh` passes before major merges.
- **Release**:
  - Verify `Data Safety` and `Ads Policy` alignment as documented in `tickets.md`.

---

## 📝 General Rules for All Agents
1. **Test First**: Every feature change (especially logic) must include or update unit tests.
2. **Deduplication**: Use deterministic IDs for seeding data to avoid duplicates.
3. **Docs**: Update `tickets.md` or `agents.md` status when a milestone is reached.
