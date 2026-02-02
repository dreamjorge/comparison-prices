# Cross-Platform Alignment Analysis
## Android ↔ Web Feature Parity

> **Goal**: Ensure both Android and Web apps offer equivalent core functionality for a consistent user experience across platforms.

---

## 📊 Current State Comparison

### Android App Status (✅ Well-Developed)

| Feature | Status | Implementation |
|---------|--------|----------------|
| **Shopping List Management** | ✅ Complete | HomeScreen + HomeViewModel + Room database |
| **Add/Remove Items** | ✅ Complete | Full CRUD operations with quantity controls |
| **Store Comparison** | ✅ Complete | CompareScreen with dynamic calculation by quantity |
| **Price Calculation** | ✅ Complete | Real-time totals respecting item quantities |
| **Settings** | ✅ Complete | SettingsScreen with data-driven sections |
| **Paywall/Premium** | ✅ Complete | PaywallScreen + PremiumViewModel + isPro state |
| **Price History** | ✅ Complete | PriceHistoryScreen (per product) |
| **Local Database** | ✅ Complete | Room with entities: Product, Store, ShoppingList, ListItem, PriceSnapshot |
| **Demo Data Seeding** | ✅ Complete | Idempotent seeding in ViewModels |
| **Navigation** | ✅ Complete | Jetpack Compose Navigation with 5 screens |
| **Ad System** | ✅ Complete | AdBanner component (conditional on isPro) |
| **Material Design** | ✅ Complete | Material 3 with theming |

### Web App Status (❌ Minimal Implementation)

| Feature | Status | Implementation |
|---------|--------|----------------|
| **Shopping List Management** | ❌ Missing | No list UI at all |
| **Add/Remove Items** | ❌ Missing | No product search or item management |
| **Store Comparison** | 🟡 Stub | Hardcoded demo data in ComparePage |
| **Price Calculation** | ❌ Missing | Static totals, no real calculation |
| **Settings** | ❌ Missing | No settings page |
| **Paywall/Premium** | ❌ Missing | No monetization UI |
| **Price History** | ❌ Missing | No history visualization |
| **Local/Remote State** | ❌ Missing | No state management (Context API, Redux, etc.) |
| **API Integration** | 🟡 Partial | `api.ts` with contracts, but not used in UI |
| **Navigation** | ✅ Basic | React Router with 3 routes (Home, Compare, NotFound) |
| **Design System** | ❌ Missing | Basic CSS, no component library |
| **Responsive Design** | ❌ Missing | Not mobile-optimized |

---

## 🎯 Feature Parity Gaps

### Critical (Blocking Demo)
1. **No shopping list UI** - Core feature missing
2. **No product search/add** - Can't build lists
3. **No real price comparison** - Only hardcoded stubs
4. **No settings page** - Can't configure preferences

### Important (UX Consistency)
5. **No premium/paywall flow** - Monetization missing
6. **No price history charts** - Missing insight feature
7. **No responsive design** - Poor mobile experience
8. **No design system** - Inconsistent UI vs Android

### Nice-to-Have (Enhancements)
9. **No dark theme toggle** - Android has Material themes
10. **No state management** - Will cause bugs at scale
11. **No error handling** - API failures not handled

---

## 📋 Alignment Tickets

### Epic 15 — Android-Web Feature Parity
**Priority:** P0  
**Goal:** Bring web app to feature parity with Android MVP

---

#### TICKET 15.1 — Web Shopping List Management (Match Android HomeScreen)
**Type:** Feature | **Priority:** P0 | **Owner:** Web Team

**Description**  
Replicate Android's HomeScreen functionality in web app.

**Android Reference:** `HomeScreen.kt` (176 lines)

**Android Features to Match:**
- Display active list name and item count
- List of items with product name, brand, quantity, unit
- Add/remove items
- Increment/decrement quantities inline
- Delete items
- Empty state message
- Navigation to price history (per product)
- Ad banner for free users

**Criterios de aceptación**
- [ ] Match Android's card-based layout
- [ ] Same item display format (product name, brand, quantity controls)
- [ ] Delete button with confirmation
- [ ] Empty state: "Tu lista esta vacia"
- [ ] Fetch list from `/api/lists/:id`
- [ ] POST/DELETE/PATCH for item operations
- [ ] Responsive on mobile

**Data Structure (from Android):**
```kotlin
data class ShoppingList(id, name, createdAt)
data class ListItem(id, listId, productId, quantity, unit)
data class ListItemWithProduct(item, product)
```

---

#### TICKET 15.2 — Web Store Comparison (Match Android CompareScreen)
**Type:** Feature | **Priority:** P0 | **Owner:** Web Team

**Description**  
Replicate Android's CompareScreen calculation logic and UI.

**Android Reference:** `CompareScreen.kt` (365 lines)

**Android Features to Match:**
- Display list name and current date at top
- Search/filter stores by name or product
- Show total per store (sorted cheapest first)
- Calculate totals using item quantities
- Show "Ahorro vs siguiente" (savings vs next cheapest)
- Highlight cheapest store with badge "Más barato"
- Show per-store item breakdown
- Empty state for no results

**Criterios de aceptación**
- [ ] Use same calculation logic as Android:
  - `buildQuantityByProduct()` to aggregate quantities
  - `storeTotalValue()` to calculate weighted totals
  - `buildStoreComparisons()` to compute savings
- [ ] Search input filters stores/products
- [ ] Sort stores by total (cheapest first)
- [ ] Show savings badge on best option
- [ ] Per-store item list showing product + price
- [ ] Currency formatting matching user locale
- [ ] Date formatting: "d MMM yyyy"

**API Endpoint:**
```typescript
POST /api/list-totals
Body: { items: [{productId, quantity}] }
Response: { totals: [{storeId, storeName, total, items}] }
```

---

#### TICKET 15.3 — Web Settings Page (Match Android SettingsScreen)
**Type:** Feature | **Priority:** P1 | **Owner:** Web Team

**Description**  
Replicate Android's SettingsScreen structure and content.

**Android Reference:** `SettingsScreen.kt` + `SettingsContent.kt`

**Android Sections to Match:**
1. **Preferencias de compra**
   - Zona: "Centro - CDMX"
   - Tiendas activas: "Walmart, Soriana, Chedraui"

2. **Alertas y precios**
   - Alertas: "Baja de precio semanal"
   - Meta de ahorro: "Ahorrar $120 esta semana"

3. **Plan**
   - Plan actual: "Free con anuncios"
   - Upgrade: "Pro: sin anuncios + historial extendido"

4. **Acerca de**
   - Version: "MVP 0.1"
   - Soporte: "hola@comparador.app"

**Criterios de aceptación**
- [ ] Use same section structure as Android
- [ ] Icons matching Android (`LocationOn`, `Storefront`, `NotificationsActive`, `Payments`, `Info`)
- [ ] Card-based layout per section
- [ ] Editable fields (zone, stores, alerts)
- [ ] Save preferences to localStorage or backend
- [ ] Match Android's Material 3 design

---

#### TICKET 15.4 — Web Price History (Match Android PriceHistoryScreen)
**Type:** Feature | **Priority:** P1 | **Owner:** Web Team

**Description**  
Replicate Android's PriceHistoryScreen for per-product price tracking.

**Android Reference:** `PriceHistoryScreen.kt`

**Android Features to Match:**
- Show product name and brand
- Display current price (latest snapshot)
- Line chart showing price over time (7/30/90 days)
- Highlight lowest/highest points
- Show price change percentage
- Time range selector

**Criterios de aceptación**
- [ ] Fetch from `/api/products/:id/history`
- [ ] Line chart with Chart.js or Recharts
- [ ] Time range toggle (7d, 30d, 90d)
- [ ] Show current price prominently
- [ ] Annotations for min/max prices
- [ ] Mobile-friendly chart (touch zoom)

---

#### TICKET 15.5 — Web Paywall/Premium UI (Match Android PaywallScreen)
**Type:** Feature | **Priority:** P1 | **Owner:** Web Team

**Description**  
Replicate Android's PaywallScreen for premium upsell.

**Android Reference:** `PaywallScreen.kt` + `PremiumViewModel.kt`

**Android Features to Match:**
- Free plan description with limitations
- Pro plan benefits:
  - Sin anuncios
  - Historial extendido (unlimited)
  - Alertas personalizadas
  - Soporte prioritario
- "Upgrade" button
- "Continuar gratis" option
- Show plan status in settings

**Criterios de aceptación**
- [ ] Modal or dedicated page for paywall
- [ ] List Pro benefits matching Android
- [ ] "Upgrade" CTA button
- [ ] "Continue with ads" option
- [ ] Store isPro state globally (Context/Redux)
- [ ] Conditionally hide ads if isPro

---

#### TICKET 15.6 — Web State Management (Match Android ViewModel Pattern)
**Type:** Task | **Priority:** P0 | **Owner:** Web Team

**Description**  
Implement global state management mirroring Android's ViewModel architecture.

**Android Reference:** `HomeViewModel.kt`, `CompareViewModel.kt`, `PremiumViewModel.kt`

**State to Manage:**
- Active shopping list (with items)
- User preferences (zone, active stores, isPro)
- Price comparison results
- Loading/error states

**Criterios de aceptación**
- [ ] Choose state library (Context API + useReducer, or Redux, or Zustand)
- [ ] Create stores/hooks for:
  - `useShoppingList()` - active list, items, CRUD operations
  - `usePreferences()` - zone, stores, theme, isPro
  - `useComparison()` - store totals, calculations
- [ ] Persist critical state to localStorage
- [ ] Handle loading/error states per Android pattern

**Recommended:** Use **Zustand** (lightweight, TypeScript-friendly)

---

#### TICKET 15.7 — Web Design System Alignment
**Type:** Task | **Priority:** P1 | **Owner:** Web Team

**Description**  
Create CSS/component library matching Android's Material 3 design.

**Android Reference:** Material 3 colors, typography, spacing from `theme/` (if exists)

**Components Needed (from Android):**
- Card (elevated, outlined)
- Button (primary, secondary, text)
- TextField / OutlinedTextField
- IconButton
- Badge / Surface chips
- Typography scale
- Color tokens (primary, secondary, surface, error, etc.)

**Criterios de aceptación**
- [ ] CSS variables or styled-components matching Material 3
- [ ] Color palette extraction from Android theme
- [ ] Typography scale (headlineSmall, titleLarge, bodyMedium, etc.)
- [ ] Spacing system (4dp, 8dp, 12dp, 16dp)
- [ ] Component library matching Android's Compose components
- [ ] Dark/light theme support

---

## 🗺️ Implementation Roadmap

### Phase 1: Core Parity (Weeks 1-2)
**Goal:** Match Android's MVP functionality

| Ticket | Priority | Effort | Dependencies |
|--------|----------|--------|--------------|
| 15.6 - State Management | P0 | 2 days | None |
| 15.7 - Design System | P1 | 3 days | None |
| 15.1 - Shopping List | P0 | 4 days | 15.6, 15.7 |
| 15.2 - Store Comparison | P0 | 3 days | 15.1, 15.6 |

### Phase 2: Premium & Settings (Week 3)
**Goal:** Complete feature set

| Ticket | Priority | Effort | Dependencies |
|--------|----------|--------|--------------|
| 15.3 - Settings Page | P1 | 2 days | 15.6, 15.7 |
| 15.5 - Paywall/Premium | P1 | 2 days | 15.6, 15.7 |
| 15.4 - Price History | P1 | 2 days | 15.7 (charts) |

### Phase 3: Polish & Mobile (Week 4)
**Goal:** Production-ready

| Ticket | Priority | Effort | Dependencies |
|--------|----------|--------|--------------|
| 13.1 - Responsive Design | P1 | 3 days | All above |
| 13.2 - PWA Setup | P1 | 1 day | 13.1 |
| 14.1 - Animations | P2 | 1 day | All above |

---

## 📐 Technical Alignment Details

### Data Models (Match Android Room Entities)

```typescript
// Match Android Room entities
interface Product {
  id: number;
  name: string;
  brand: string | null;  // Match Android nullable brand
  category: string;
  unit: string;  // "kg", "litros", "unidades"
}

interface ShoppingList {
  id: number;
  name: string;
  createdAt: string;  // ISO date
}

interface ListItem {
  id: number;
  listId: number;
  productId: number;
  quantity: number;  // Double in Android
  unit: string;
}

interface ListItemWithProduct {
  item: ListItem;
  product: Product;
}

interface StoreTotal {
  storeId: string;
  storeName: string;
  zone: string;
  total: number;  // Int (cents/centavos)
  items: Array<{ product: string; price: string }>;
}
```

### API Contracts (Already Defined in `@comparison-prices/contracts`)

The OpenAPI spec already exists and is consumed by Android. Web must use **identical contracts**:

```typescript
// From app-web/src/services/api.ts
import { openApiSpec } from "@comparison-prices/contracts";

// Ensure type alignment
export type Store = typeof openApiSpec.components.schemas.Store & { id: string };
export type Product = typeof openApiSpec.components.schemas.Product & { id: string };
export type StoreTotal = typeof openApiSpec.components.schemas.StoreTotal & { storeId: string };
```

### Business Logic Alignment

**Critical:** Replicate Android's calculation functions in TypeScript:

**From `CompareScreen.kt`:**
```kotlin
// Android implementation
fun buildQuantityByProduct(listItems: List<ListItemWithProduct>): Map<String, Double>
fun storeTotalValue(store: StorePrice, quantityByProduct: Map<String, Double>): Int
fun buildStoreComparisons(storePrices: List<StorePrice>, quantityByProduct: Map<String, Double>): List<StoreComparison>
```

**Web should implement:**
```typescript
// app-web/src/utils/comparison.ts
function buildQuantityByProduct(listItems: ListItemWithProduct[]): Map<string, number>;
function storeTotalValue(store: StorePrice, quantityByProduct: Map<string, number>): number;
function buildStoreComparisons(storePrices: StorePrice[], quantityByProduct: Map<string, number>): StoreComparison[];
```

---

## ✅ Success Metrics

### Feature Parity Checklist
- [ ] Web can create and manage shopping lists (like Android HomeScreen)
- [ ] Web shows real-time store totals (like Android CompareScreen)
- [ ] Web has settings matching Android's 4 sections
- [ ] Web displays price history charts
- [ ] Web has paywall/premium flow
- [ ] Web uses identical API contracts as Android
- [ ] Web calculates totals using same logic as Android
- [ ] Web design matches Material 3 aesthetic

### UX Consistency
- [ ] Same terminology (Spanish, LATAM localization)
- [ ] Same iconography (Material Icons)
- [ ] Same color scheme (Material 3)
- [ ] Same navigation patterns
- [ ] Same error messages

### Performance
- [ ] Web app loads in < 2s
- [ ] Works offline (PWA with service worker)
- [ ] Responsive on mobile (< 640px)
- [ ] Accessible (WCAG AA)

---

## 🔄 Maintenance Strategy

### Keep in Sync
1. **Shared Contracts**: Use `@comparison-prices/contracts` package for both platforms
2. **Shared Logic**: Document algorithm changes in both codebases
3. **Design Tokens**: Export Android theme to JSON, consume in web CSS
4. **Testing**: Cross-platform E2E tests validating same outputs

### Future Enhancements (Apply to Both)
- New features must be implemented in **both** Android and Web
- UI changes should maintain Material 3 consistency
- API changes require updates to shared contracts
