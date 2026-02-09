# Work Context - Comparison Prices Project

**Last Updated:** 2026-02-08
**Branch:** `codex/mvp-api-auth-pagination`
**Latest Commit:** `e4d9aba` - "feat: integrate PROFECO API with real Mexican grocery price data"

---

## 🎯 Current Project State

### Recently Completed (This Session)

**TICKET 3.3 - Real Price Data Integration ✅**

Successfully integrated PROFECO (Mexican government) API to fetch real grocery store prices:

- **Data Source:** PROFECO "Quién es Quién en los Precios" 2025 dataset
- **Records Fetched:** 923,545 price records from November 2025
- **Coverage:** All Mexican states with real store data
- **Stores in DB:** 7 (Soriana, Walmart, Bodega Aurrera, Chedraui, Farmacia Guadalajara, etc.)
- **Products in DB:** 764 unique products with brands, sizes, categories
- **Price Snapshots:** 1,000 (test dataset - full 923k available)

### Technical Implementation

#### Backend Architecture
```
api/
├── src/
│   ├── db/
│   │   ├── database.ts      # SQLite operations (better-sqlite3)
│   │   └── schema.ts        # Table definitions + indexes
│   ├── providers/
│   │   ├── DataProvider.ts  # Interface for price data sources
│   │   ├── PROFECOProvider.ts  # Mexican govt API integration
│   │   └── ScraperProvider.ts  # Future web scrapers
│   ├── services/
│   │   └── dataSync.ts      # Auto-refresh every 24h
│   └── index.ts             # Express API with auth + pagination
├── data/
│   └── prices.db           # SQLite database (WAL mode)
└── test-data-fetch.js      # Test script for data fetching
```

#### API Endpoints (All Working ✅)
- `GET /v1/stores` - List stores with pagination, optional state filter
- `GET /v1/search` - Search products by name/brand with pagination
- `GET /v1/price-history` - Get price history for a product
- `POST /v1/list-totals` - Calculate shopping list totals per store

#### Key Features
- **Authentication:** X-API-Key header validation
- **Pagination:** Cursor-based (base64 encoded offsets)
- **Fallback:** Auto-fallback to mock data when DB is empty
- **Data Sync:** Automatic refresh from PROFECO every 24 hours
- **State Filtering:** Filter stores/products by Mexican state
- **Performance:** Proper indexes on stores.state, products.name, price_snapshots

### PROFECO Provider - Important Details

**CSV Parsing Fixes:**
- Normalizes all column headers to UPPERCASE (PROFECO uses lowercase)
- Handles field name variations: `FECHA_REGISTRO` / `FECHAREGISTRO` / `FECHA`
- 120-second timeout for large files (Nov 2025 data = 153MB per file)
- Lookback period: 4 months to find available data
- Avoids stack overflow by using loop instead of spread operator for 900k+ records

**Data Format:**
```
Column Headers (normalized to uppercase):
- PRODUCTO, PRESENTACION, MARCA, CATEGORIA
- PRECIO, FECHA_REGISTRO
- CADENA_COMERCIAL, ESTADO, MUNICIPIO, DIRECCION
```

**URL Pattern:**
```
https://repodatos.atdt.gob.mx/api_update/profeco/programa_quien_es_quien_precios_2025/{MM}-{YYYY}_0{1|2}.csv
```

**Latest Available Data:** November 2025 (`11-2025_01.csv`, `11-2025_02.csv`)

### Environment Configuration

**Required `.env` variables:**
```bash
PORT=4000
NODE_ENV=development
API_KEY=dummy-dev-key  # Change for production
CORS_ORIGINS=http://localhost:4173,http://127.0.0.1:4173
PROFECO_BASE_URL=https://repodatos.atdt.gob.mx/api_update/profeco/programa_quien_es_quien_precios_2025
DATA_REFRESH_HOURS=24
DB_PATH=./data/prices.db
```

### Dependencies Added
```json
{
  "better-sqlite3": "^11.8.1",
  "csv-parse": "^5.6.0"
}
```

---

## 📋 Next Steps & Pending Tickets

### High Priority Backend/API Work

**TICKET 3.4 - Location-based Pricing (P1)**
- Add geolocation support to API endpoints
- Filter stores/products by proximity to user location
- Implement radius-based fallback when no nearby stores
- Already have state/municipality data - need to add lat/lng

**TICKET 3.2 - Product Normalization (P1)**
- Match similar products across stores
- Handle size/unit variations (946ml vs 1L)
- Fuzzy matching for brand names

**Other Options:**
- Sync full 923k records to database (currently only 1000)
- Create PR to merge branch into `main`
- Integrate API with web app (`app-web/`)
- Test API with Android app

### Completed Tickets (Recent)
- ✅ TICKET 3.3 - Real price data fetch (this session)
- ✅ TICKET 5.2 - Shopping list alerts
- ✅ TICKET 6.3 - Paywall Pro
- ✅ TICKET 7.1, 7.2, 7.4 - QA and Play Store prep
- ✅ TICKET 8.6, 8.7, 8.8 - Data normalization fixes

---

## 🔧 How to Test/Run

### Start API Server
```bash
cd api
npm install
npm run build
PORT=4000 API_KEY=test-key node dist/api/src/index.js
```

### Test Data Fetch
```bash
cd api
npm run build
node test-data-fetch.js  # Fetches and syncs 1000 records
```

### Check Database
```bash
cd api
node check-db.js  # Shows stats and sample data
```

### Test API Endpoints
```bash
# List stores
curl -H "X-API-Key: test-key" "http://localhost:4000/v1/stores?limit=5"

# Search products
curl -H "X-API-Key: test-key" "http://localhost:4000/v1/search?q=aceite&limit=5"

# Price history
curl -H "X-API-Key: test-key" "http://localhost:4000/v1/price-history?productId=prod-aceite-precissimo-botella-850-ml-vegetal"
```

---

## 🗂️ Project Structure

```
comparison-prices/
├── api/                    # Backend API (Node.js + Express)
│   ├── src/
│   │   ├── db/            # Database layer
│   │   ├── providers/     # Data source providers
│   │   ├── services/      # Background services
│   │   ├── index.ts       # Main API server
│   │   └── mockData.ts    # Fallback mock data
│   ├── data/              # SQLite database files
│   └── dist/              # Compiled JavaScript
├── app-web/               # Web app (React + Vite)
├── app-android/           # Android app (Kotlin + Compose)
├── packages/
│   └── contracts/         # Shared TypeScript types
├── tickets.md             # Full backlog and roadmap
└── WORK_CONTEXT.md        # This file
```

---

## 💡 Important Notes

### Database Schema
- **stores:** id, name, logo_url, currency, region, state, municipality, address
- **products:** id, name, brand, size_label, image_url, category
- **price_snapshots:** id, product_id, store_id, price, captured_at, is_promo

### Known Issues/Limitations
1. Database only has 1,000 records (test data) - full dataset available (923k)
2. No geolocation data yet (state/municipality only)
3. PROFECO data lags ~1-2 months behind current date
4. No product normalization yet (exact match only)

### Performance
- SQLite WAL mode enabled for better concurrency
- Indexes on: stores.state, products.name, price_snapshots (product_id, store_id, captured_at)
- Cursor-based pagination prevents large offset queries

---

## 🎓 Context for Future Sessions

When resuming work:
1. **Check latest commit:** `git log --oneline -5`
2. **Read this file:** Understand current state
3. **Check tickets.md:** See full backlog
4. **Test API:** Verify everything still works
5. **Choose next ticket:** Prioritize P0/P1 items

**Key Files to Reference:**
- `api/src/providers/PROFECOProvider.ts` - PROFECO integration logic
- `api/src/db/database.ts` - All database operations
- `api/src/index.ts` - API endpoints and auth
- `tickets.md` - Full project roadmap

**Quick Commands:**
```bash
# Check database contents
cd api && node check-db.js

# Fetch latest PROFECO data
cd api && npm run build && node test-data-fetch.js

# Start API
cd api && PORT=4000 API_KEY=test-key node dist/api/src/index.js
```

---

**End of Context** - Resume work by checking tickets.md for next priority items.
