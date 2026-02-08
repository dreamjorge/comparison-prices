# Project Skills & Automation

This document defines the specialized skills, automated tasks, and development plugins for each component in the **Comparison Prices (LATAM)** monorepo.

---

## Architecture Overview

```
comparison-prices/
├── api/              # Node.js/Express API — reference implementation (legacy, no CI)
├── api-python/       # Python/FastAPI API — PRIMARY backend, active CI
├── app-android/      # Android app (Kotlin + Jetpack Compose), offline-first
├── app-web/          # React SPA (Vite + TypeScript), uses Python API
├── matching-rust/    # Rust fuzzy matching engine (Jaro-Winkler), future JNI
├── packages/
│   └── contracts/    # Shared OpenAPI spec + generated TypeScript types
└── docker-compose.yml # Python API only (PostgreSQL commented out for Phase 2)
```

**Primary API**: `api-python/` — Python FastAPI, port 8000, 210+ mock products
**Reference API**: `api/` — Node.js Express, port 4000, 5 mock products — **NOT in CI, consider deprecating**

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

### Screens
| Screen | File | Notes |
|--------|------|-------|
| Home | `ui/home/HomeScreen.kt` | Shopping list, FAB add item, ad banner |
| Comparador | `ui/compare/CompareScreen.kt` | Totals per store, highlights cheapest |
| Historial | `ui/history/PriceHistoryScreen.kt` | Vico chart, price trends |
| Configuración | `ui/settings/SettingsScreen.kt` | Zone, favorite stores |
| Paywall | `ui/premium/PaywallScreen.kt` | Pro subscription UI (billing not wired) |

### Room Database Migrations
- Version: **4** (MIGRATION_1_2 through MIGRATION_3_4)
- Entities: `app-android/app/src/main/java/com/compareprices/data/local/Entities.kt`
- DAOs: `app-android/app/src/main/java/com/compareprices/data/local/Daos.kt`
- Database + migrations: `app-android/app/src/main/java/com/compareprices/data/local/AppDatabase.kt`
- Demo seed data: `app-android/app/src/main/java/com/compareprices/data/local/DemoSeedData.kt`
- Strategy: deterministic IDs + `insertIgnore`/`upsert` for idempotent seeding (see `docs/demo-data-dedup.md`)

### Room Entities
| Entity | Key Fields |
|--------|-----------|
| `ProductEntity` | id, name, brand?, defaultUnit (UNIQUE: name+brand+unit) |
| `StoreEntity` | id, name, zone? |
| `PriceSnapshotEntity` | id, productId, storeId, price, currency, capturedAt |
| `ShoppingListEntity` | id, name, createdAt (UNIQUE: name) |
| `ListItemEntity` | id, listId, productId, quantity, unit (UNIQUE: listId+productId) |

### WorkManager
- **PriceRefreshWorker**: Scheduled task — currently shows mock notification. Real price sync pending (Phase 2).

### Key Files
| Path | Purpose |
|------|---------|
| `app-android/app/src/main/java/.../data/local/` | Room Entities, DAOs, Migrations, DI Module |
| `app-android/app/src/main/java/.../ui/` | Compose Screens (Home, Compare, History, Settings, Paywall) |
| `app-android/app/src/main/java/.../data/work/` | WorkManager (PriceRefreshWorker) |
| `app-android/app/build.gradle.kts` | Dependencies: Compose, Hilt, Room, Material 3, Vico |
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

### Routes
| Path | Component | Description |
|------|-----------|-------------|
| `/` | `HomePage` | Dashboard: 4 cards (Lista, Tiendas, Ahorro, Alertas) |
| `/comparador` | `ComparePage` | Store comparison, highlights cheapest |
| `*` | `NotFoundPage` | 404 fallback |

### API Client (`app-web/src/api/client.ts`)
- Base URL: `VITE_API_BASE_URL` env var or `/api` (default)
- Auth: `VITE_API_KEY` env var or `dummy-dev-key` (default)
- Functions: `fetchStores()`, `searchProducts(q)`, `calculateListTotals(items)`, `fetchPriceHistory(productId)`

### Key Files
| Path | Purpose |
|------|---------|
| `app-web/src/main.tsx` | React root |
| `app-web/src/router.tsx` | React Router v6 (routes: `/`, `/comparador`, `*`) |
| `app-web/src/layouts/AppLayout.tsx` | Shared header, nav, footer |
| `app-web/src/pages/` | HomePage, ComparePage, NotFoundPage |
| `app-web/src/api/client.ts` | Fetch wrapper, 4 API functions |
| `app-web/src/__tests__/` | Vitest test files |

### Dependencies
- React 18.3, React Router 6.26, **Vite 7.3.1**, **Vitest 4.0.18**, TypeScript 5.5, ESLint 9.9

---

## Python API (Agent 3) — PRIMARY BACKEND
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
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `GET` | `/stores` | Required | List 3 LATAM stores (Walmart, Chedraui, Soriana) |
| `GET` | `/search?q=...` | Required | Search 210+ mock products (cursor pagination) |
| `POST` | `/list-totals` | Required | Calculate totals per store for shopping list |
| `GET` | `/price-history?productId=...` | Optional | 3-point mock price history |

### Data Normalization (`services/normalization.py`)
- Brand cleaning: lowercase, strip special chars
- Unit conversion: ml→L, g→kg, pz (pieces)
- Search key generation: `brand + name` normalized

### Key Files
| Path | Purpose |
|------|---------|
| `api-python/main.py` | FastAPI app, 4 endpoints, 210+ mock products, auth |
| `api-python/services/normalization.py` | NormalizationService |
| `api-python/requirements.txt` | Python dependencies |
| `api-python/pyproject.toml` | pytest + ruff config |
| `api-python/tests/test_api.py` | 8 endpoint tests |
| `api-python/tests/test_normalization.py` | 8 normalization tests |
| `api-python/Dockerfile` | Alpine-based container image |

---

## Node.js API (api/) — REFERENCE / LEGACY
**Responsibility**: Mirror of Python API, used as reference implementation. **NOT active in CI.**

> ⚠️ **Status**: Redundant with `api-python/`. Consider deprecating. No tests. Not used by web or Android.

### Run
```bash
cd api
npm install
npm run dev                   # nodemon + ts-node on http://localhost:4000
npm run build                 # tsc compile
```

### Endpoints (all under `/v1/` prefix)
| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/v1/stores` | List stores (cursor pagination) |
| `GET` | `/v1/search?q=...` | Search 5 mock products |
| `POST` | `/v1/list-totals` | Calculate totals per store |
| `GET` | `/v1/price-history?productId=...` | Price history |

### Key Files
| Path | Purpose |
|------|---------|
| `api/src/index.ts` | Express app, Zod validation, 4 endpoints |
| `api/src/mockData.ts` | 5 products, 3 stores, 12 price snapshots |

---

## Shared Contracts (Agent 2 / Agent 3)
**Responsibility**: Single source of truth for API types across web and backend.

### Generate Types from OpenAPI
```bash
cd packages/contracts
npm run generate              # Reads openapi.json -> writes src/generated.ts
```

### Types
| Type | Key Fields |
|------|-----------|
| `Store` | id, name, logoUrl, currency (MXN), region |
| `Product` | id, name, brand?, sizeLabel?, imageUrl?, category? |
| `PriceSnapshot` | productId, storeId, price, capturedAt, isPromo |
| `ListItem` | productId, quantity, unitLabel? |
| `StoreTotal` | storeId, total, updatedAt, savings? |

### Validation
- After editing `packages/contracts/openapi.json`, always regenerate types.
- CI validates that `src/generated.ts` matches the current `openapi.json` (contract drift check).
- All API calls in `app-web/` must use types from `@comparison-prices/contracts`.

### Key Files
| Path | Purpose |
|------|---------|
| `packages/contracts/openapi.json` | OpenAPI 3.0.3 spec (source of truth) |
| `packages/contracts/src/types.ts` | TypeScript interfaces (5 types) |
| `packages/contracts/src/generated.ts` | Auto-generated from spec |
| `packages/contracts/src/index.ts` | Re-exports |
| `packages/contracts/scripts/generate.mjs` | Generation script |

---

## Rust Matching Engine (Agent 4)
**Responsibility**: High-performance fuzzy matching for LATAM product names.

### Build & Test
```bash
cd matching-rust
cargo build                   # Build library
cargo build --release         # Release build (optimized)
cargo test                    # Run unit tests (5 tests)
```

### Algorithm
1. **Jaro-Winkler** similarity (`strsim` crate) on `brand + name`
2. **Exact brand match boost**: +0.1 (clamped to 1.0)
3. **Word-level match bonus**: `0.8 * jaro_winkler + 0.2 * word_score`
4. **Threshold**: score > 0.4 to include in results
5. **Output**: top-N candidates sorted by score descending

### Public API
```rust
pub fn clean_text(text: &str) -> String
pub fn calculate_score(query: &str, candidate: &ProductCandidate) -> f64
pub fn match_candidates(query: &str, candidates: &[ProductCandidate], top_n: usize) -> Vec<MatchResult>
```

### JNI/FFI Integration
- `crate-type = ["lib", "cdylib"]` enables dynamic library output
- **Status**: JNI bridge NOT yet implemented — future task for Android integration
- Rust library compiles standalone; no Android-side binding code exists yet

### Key Files
| Path | Purpose |
|------|---------|
| `matching-rust/src/lib.rs` | Core matching logic + 5 inline tests |
| `matching-rust/Cargo.toml` | Dependencies: strsim 0.11, serde 1.0 |

---

## CI/CD Pipeline (Agent 6)
**Responsibility**: Automated build, test, and release.

### Pipeline (`.github/workflows/ci.yml`)
Triggers on: push to `main`, pull requests to `main`.

| Job | Steps |
|-----|-------|
| **Android CI** | DevContainer → `run-ci-local.sh` (lint + test + assembleDebug) → Upload APK |
| **Web CI** | Node 20 → npm install → vitest --run → eslint → build |
| **Python API CI** | Python 3.12 → pip install → ruff check → pytest |
| **Rust CI** | Rust stable → cargo cache → cargo test → cargo build --release |
| **Contracts CI** | Node 20 → npm run verify → npm run generate → git diff --exit-code |

> ⚠️ **Node.js API** (`api/`) has no CI job. No tests exist.

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
docker compose up             # Start Python API
docker compose up api         # Start only the Python API
```

### Services
| Service | Port | Description |
|---------|------|-------------|
| `api` | 8000 | FastAPI backend (Python, hot-reload) |
| `db` | 5432 | PostgreSQL 16 (Phase 2, TICKET 11.2 — commented out) |

---

## Developer Tools

### qmd — Local Knowledge Search
Indexes the codebase for semantic/hybrid search. Useful for finding patterns across all files.

```bash
# Search indexed codebase
qmd search "authentication API key"
qmd query "how does pagination work"     # Semantic search with reranking
qmd vsearch "fuzzy matching algorithm"   # Vector similarity search

# Index management
qmd collection list
qmd update                               # Re-index after changes
qmd embed                                # Rebuild vector embeddings
```

Collection `comparison-prices` indexes: `**/*.{ts,tsx,py,kt,rs,md,json,toml}` (96 files)

### codegraph — Code Graph Analysis
Installed as dev dependency in `api/`, `app-web/`, `packages/contracts/`.

```bash
# Use programmatically in Node.js/TypeScript
import codegraph from 'codegraph'
```

---

## Authentication Plugin
**Responsibility**: User authentication across platforms.

### Current State (MVP: Mock)
- API: hardcoded `X-API-Key: dummy-dev-key` header check (both Python and Node.js APIs)
- Web: sends mock key via `VITE_API_KEY` env var
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
- Trend graphs in Android UI (Vico charts already integrated)
- Textual recommendations (e.g., "Prices usually drop on Tuesdays")

---

## Known Tech Debt
| Item | Location | Priority |
|------|----------|----------|
| Dual API implementations (Node.js + Python) | `api/` vs `api-python/` | High — consolidate |
| No JNI bridge for Rust→Android | `matching-rust/` | Medium |
| PriceRefreshWorker is a mock | `app-android/.../data/work/` | Medium |
| No error handling/retry in web API client | `app-web/src/api/client.ts` | Medium |
| Google Play Billing not wired | `app-android/.../ui/premium/` | Medium |
| No E2E tests anywhere | — | Low |
| Node.js API missing CI/tests | `api/` | Low (if keeping) |

---

## General Rules for All Agents
1. **Test First**: Every feature change must include or update unit tests.
2. **Deduplication**: Use deterministic IDs for seeding data to avoid duplicates (see `docs/demo-data-dedup.md`).
3. **Docs**: Update `tickets.md` or `agents.md` status when a milestone is reached.
4. **Contracts**: After changing API endpoints, update `packages/contracts/openapi.json` and regenerate types.
5. **Security**: Never commit secrets (API keys, keystore passwords). Use `.env` files and `keystore.properties` (gitignored).
6. **Offline-First**: Android app must work without network. Room DB is the local source of truth.
7. **LATAM Context**: Product names, brands, and units are in Spanish. Normalization must handle accents, abbreviations, and regional variations.
8. **Primary API**: All new backend work goes in `api-python/`. The Node.js `api/` is a reference only.
