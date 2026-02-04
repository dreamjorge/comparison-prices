# Project Skills & Automation

This document defines the specialized skills, automated tasks, and development plugins for each component in the **Comparison Prices (LATAM)** monorepo.

---

## Android Core (Agent 2)
**Responsibility**: Jetpack Compose UI, Room DB, WorkManager, Hilt DI, and local business logic.

### Build & Test
```bash
cd app-android
./gradlew :app:lint          # Lint checks (Android Lint)
./gradlew :app:test          # Unit tests (JUnit + Compose)
./gradlew :app:assembleDebug # Debug APK
./gradlew bundleRelease      # Release AAB (requires keystore.properties)
```

### Full CI Locally
```bash
cd app-android
./scripts/run-ci-local.sh    # Runs lint + test + assembleDebug
```

### Container-Based Development
```bash
cd app-android
./scripts/container-setup.sh      # Build the Docker image
./scripts/container-shell.sh      # Interactive shell in container
./scripts/container-ci.sh         # Run CI inside container
./scripts/container-test.sh       # Run tests inside container
./scripts/container-build-apk.sh  # Build APK inside container
```

### DevContainer (VS Code)
- Config: `app-android/.devcontainer/devcontainer.json`
- Dockerfile: `app-android/container/Dockerfile`
- Base: Eclipse Temurin JDK 17, Android SDK API 36, Gradle 9.1.0

### Room Database Migrations
- Entities: `app-android/app/src/main/java/com/compareprices/data/local/Entities.kt`
- DAOs: `app-android/app/src/main/java/com/compareprices/data/local/Daos.kt`
- Database + migrations: `app-android/app/src/main/java/com/compareprices/data/local/AppDatabase.kt`
- Demo seed data: `app-android/app/src/main/java/com/compareprices/data/local/DemoSeedData.kt`
- Strategy: deterministic IDs + `insertIgnore`/`upsert` for idempotent seeding (see `docs/demo-data-dedup.md`)

### Key Files
| Path | Purpose |
|------|---------|
| `app-android/app/src/main/java/.../data/local/` | Room Entities, DAOs, Migrations, DI Module |
| `app-android/app/src/main/java/.../ui/` | Compose Screens (Home, Compare, Settings) |
| `app-android/app/src/main/java/.../data/work/` | WorkManager (PriceRefreshWorker) |
| `app-android/app/build.gradle.kts` | Dependencies: Compose, Hilt, Room, Material 3 |
| `app-android/gradle/libs.versions.toml` | Centralized dependency versions |

---

## Web Frontend (Agent 2 / Agent 3)
**Responsibility**: React + Vite + TypeScript SPA with shared contract types.

### Development
```bash
cd app-web
npm install                   # Install dependencies
npm run dev                   # Vite dev server on http://localhost:4173
npm run build                 # TypeScript check + Vite production build
npm run preview               # Preview production build
```

### Testing & Linting
```bash
cd app-web
npm run test                  # Vitest (unit tests, watch mode)
npm run test -- --run         # Single run (CI mode)
npm run lint                  # ESLint (strict: --max-warnings=0)
```

### Key Files
| Path | Purpose |
|------|---------|
| `app-web/src/main.tsx` | React root |
| `app-web/src/router.tsx` | React Router v6 (routes: `/`, `/comparador`, `*`) |
| `app-web/src/layouts/AppLayout.tsx` | Shared header, nav, footer |
| `app-web/src/pages/` | HomePage, ComparePage, NotFoundPage |
| `app-web/src/__tests__/` | Vitest test files |

### Dependencies
- React 18.3, React Router 6.26, Vite 5.4, Vitest 2.0, TypeScript 5.5, ESLint 9.9

---

## Shared Contracts (Agent 2 / Agent 3)
**Responsibility**: Single source of truth for API types across web and backend.

### Generate Types from OpenAPI
```bash
cd packages/contracts
npm run generate              # Reads openapi.json -> writes src/generated.ts
```

### Validation
- After editing `packages/contracts/openapi.json`, always regenerate types.
- CI validates that `src/generated.ts` matches the current `openapi.json` (contract drift check).
- All API calls in `app-web/` must use types from `@comparison-prices/contracts`.

### Key Files
| Path | Purpose |
|------|---------|
| `packages/contracts/openapi.json` | OpenAPI 3.0.3 spec (source of truth) |
| `packages/contracts/src/generated.ts` | Auto-generated TypeScript types |
| `packages/contracts/src/index.ts` | Re-exports generated spec |
| `packages/contracts/scripts/generate.mjs` | Generation script |

---

## Python API (Agent 3)
**Responsibility**: FastAPI backend for price data, normalization, and search.

### Setup & Run
```bash
cd api-python
pip install -r requirements.txt       # Install dependencies
python main.py                         # Start API on http://0.0.0.0:8000
# or
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

### Testing & Linting
```bash
cd api-python
python -m pytest tests/ -v            # Run unit tests
python -m ruff check .                 # Lint (ruff)
python -m ruff format --check .        # Format check
```

### API Documentation
- FastAPI auto-generates OpenAPI docs at `http://localhost:8000/docs` (Swagger UI)
- ReDoc available at `http://localhost:8000/redoc`

### Authentication (Current: Mock)
- Header-based: `X-API-Key: dummy-dev-key`
- CORS restricted to `http://localhost:4173`
- Planned: Firebase Auth (TICKET 12.1 in tickets.md)

### Endpoints
| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/stores` | List available stores |
| `GET` | `/search?q=...` | Search products (with cursor pagination) |
| `POST` | `/list-totals` | Calculate totals per store for a shopping list |
| `GET` | `/price-history?productId=...` | Price history for a product |

### Data Normalization
- `services/normalization.py`: Brand cleaning (lowercase, strip special chars), unit conversion (ml->L, g->kg, pz), search key generation

### Key Files
| Path | Purpose |
|------|---------|
| `api-python/main.py` | FastAPI app, endpoints, mock data, auth |
| `api-python/services/normalization.py` | NormalizationService (brand, units, search keys) |
| `api-python/requirements.txt` | Python dependencies |
| `api-python/pyproject.toml` | pytest + ruff config |
| `api-python/tests/` | API and normalization tests |
| `api-python/Dockerfile` | Container image for the API |

---

## Rust Matching Engine (Agent 4)
**Responsibility**: High-performance fuzzy matching for LATAM product names.

### Build & Test
```bash
cd matching-rust
cargo build                   # Build library
cargo build --release         # Release build (optimized)
cargo test                    # Run unit tests
```

### Algorithm
- **Jaro-Winkler** similarity (`strsim` crate) on combined `brand + name`
- Exact brand match boost (+0.1)
- Word-level match bonus (20% weight)
- Threshold: score > 0.4 to include in results

### JNI/FFI Integration
- `crate-type = ["lib", "cdylib"]` for dynamic library output
- Planned: JNI bridge for Android native integration

### Key Files
| Path | Purpose |
|------|---------|
| `matching-rust/src/lib.rs` | Core matching logic + tests |
| `matching-rust/Cargo.toml` | Dependencies: strsim 0.11, serde 1.0 |

---

## CI/CD Pipeline (Agent 6)
**Responsibility**: Automated build, test, and release.

### Pipeline (`.github/workflows/ci.yml`)
Triggers on: push to `main`, pull requests to `main`.

| Job | Steps |
|-----|-------|
| **Android CI** | DevContainer -> `run-ci-local.sh` (lint + test + assembleDebug) -> Upload APK |
| **Web CI** | Node 20 -> npm install -> vitest --run -> eslint |
| **Python API CI** | Python 3.12 -> pip install -> ruff check -> pytest |
| **Rust CI** | Rust stable -> cargo cache -> cargo test -> cargo build --release |
| **Contracts CI** | Node 20 -> npm run generate -> git diff --exit-code (drift check) |

### Local CI Verification
```bash
# Android
cd app-android && ./scripts/run-ci-local.sh

# Web
cd app-web && npm run test -- --run && npm run lint

# Python
cd api-python && python -m pytest tests/ && python -m ruff check .

# Rust
cd matching-rust && cargo test

# Contracts
cd packages/contracts && npm run generate && git diff --exit-code src/generated.ts
```

---

## Docker & Local Services
**Responsibility**: Containerized development and multi-service local environment.

### Docker Compose (Local Dev)
```bash
# From repo root
docker compose up             # Start API + future services
docker compose up api         # Start only the Python API
```

### Services
| Service | Port | Description |
|---------|------|-------------|
| `api` | 8000 | FastAPI backend (with hot-reload) |
| `db` | 5432 | PostgreSQL (Phase 2, TICKET 11.2 - commented out) |

---

## Authentication Plugin
**Responsibility**: User authentication across platforms.

### Current State (MVP: Mock)
- API: hardcoded `X-API-Key: dummy-dev-key` header check
- Web: will send mock key via `@comparison-prices/contracts` types
- Android: no auth, local-only data

### Phase 2 Plan (TICKET 12.1)
- Firebase Auth with Google sign-in on Android and Web
- Backend validates Firebase ID tokens
- Protected endpoints: user lists, personalized history, private snapshots
- Public endpoints: store catalog, basic search

---

## Data Pipeline Plugin
**Responsibility**: Real price data ingestion (Phase 2).

### Planned Components (TICKET 11.1, 11.2)
- Price scraping service (Playwright/Python)
- PostgreSQL database replacing in-memory mock data
- Docker Compose with PostgreSQL + FastAPI
- Migration scripts for production schema

### Data Rules
- Store only metadata + price snapshots (no full catalogs)
- Respect API/feed permissions (no unauthorized scraping)
- Cache + rate limit on external sources
- Always link to data source when applicable

---

## Price Intelligence Plugin
**Responsibility**: Advanced price analysis (Phase 2).

### Planned Components (TICKET 13.1)
- Historical trend analysis on price snapshots
- "Best day to buy" predictions from pattern detection
- Trend graphs in Android UI
- Textual recommendations (e.g., "Prices usually drop on Tuesdays")

---

## General Rules for All Agents
1. **Test First**: Every feature change must include or update unit tests.
2. **Deduplication**: Use deterministic IDs for seeding data to avoid duplicates (see `docs/demo-data-dedup.md`).
3. **Docs**: Update `tickets.md` or `agents.md` status when a milestone is reached.
4. **Contracts**: After changing API endpoints, update `packages/contracts/openapi.json` and regenerate types.
5. **Security**: Never commit secrets (API keys, keystore passwords). Use `.env` files and `keystore.properties` (gitignored).
6. **Offline-First**: Android app must work without network. Room DB is the local source of truth.
7. **LATAM Context**: Product names, brands, and units are in Spanish. Normalization must handle accents, abbreviations, and regional variations.
