# Web App Demo - Epic Proposals

## Current State Analysis

### ✅ What's Implemented
- **Basic routing**: Home page and Compare page with React Router
- **API integration**: Service layer with OpenAPI contracts
- **Mock data**: Hardcoded store totals and summary cards
- **Testing**: Basic router tests with Vitest
- **CI/CD**: GitHub Actions workflow for tests and linting
- **Dev tooling**: ESLint v9 flat config, launcher scripts

### ❌ Gaps Identified
1. **No shopping list management** - core feature is missing from web
2. **No product search** - API exists but no UI
3. **Static data only** - no real API integration yet
4. **No user preferences** - can't customize stores/zones
5. **Basic UI** - needs modern design matching Android app
6. **No responsive design** - not mobile-friendly
7. **No state management** - needs global state for shopping lists
8. **No error handling** - API failures not handled gracefully

---

## 🎯 Proposed Epics for Web App Demo

### Epic 9 — Web Foundation & UI Components
**Priority:** P0  
**Goal:** Create a modern, reusable component library matching the Android app's Material 3 design

#### TICKET 9.1 — Design System Setup
**Type:** Task | **Priority:** P0 | **Owner:** Web Team

**Description**  
Establish a consistent design system with tokens, themes, and base components.

**Criterios de aceptación**
- [ ] CSS variables for colors, spacing, typography
- [ ] Dark/light theme support
- [ ] Typography scale matching Android app
- [ ] Color palette (primary, secondary, surface, etc.)
- [ ] Documentation in Storybook or similar

---

#### TICKET 9.2 — Core Component Library
**Type:** Task | **Priority:** P0 | **Owner:** Web Team

**Description**  
Build reusable UI components matching Android's Jetpack Compose design.

**Components needed:**
- Button (primary, secondary, text)
- Card (elevated, outlined)
- Input (text, search)
- Badge
- IconButton
- Empty states
- Loading states
- Error states

**Criterios de aceptación**
- [ ] All components use design tokens
- [ ] TypeScript types for all props
- [ ] Component tests with Testing Library
- [ ] Accessibility (ARIA labels, keyboard navigation)

---

#### TICKET 9.3 — Navigation & Layout Components
**Type:** Task | **Priority:** P0 | **Owner:** Web Team

**Description**  
Create navigation and layout components for web app structure.

**Criterios de aceptación**
- [ ] AppBar with logo, nav links, actions
- [ ] Bottom navigation for mobile
- [ ] Sidebar for desktop
- [ ] Page container with consistent padding
- [ ] Responsive breakpoints (mobile, tablet, desktop)

---

### Epic 10 — Shopping List Management (Web)
**Priority:** P0  
**Goal:** Allow users to create and manage shopping lists in the web app

#### TICKET 10.1 — Shopping List View
**Type:** Feature | **Priority:** P0 | **Owner:** Web Team

**Description**  
Display the active shopping list with items, quantities, and total.

**Criterios de aceptación**
- [ ] Show list name and date
- [ ] Display items with quantity and unit
- [ ] Show item count and estimated total
- [ ] Empty state when no items
- [ ] Pull data from API endpoint `/api/lists/:id`

---

#### TICKET 10.2 — Add/Remove Items
**Type:** Feature | **Priority:** P0 | **Owner:** Web Team

**Description**  
Allow users to add products to their shopping list and remove them.

**Criterios de aceptación**
- [ ] Search product by name (autocomplete)
- [ ] Add product with quantity selector
- [ ] Remove items from list
- [ ] Update quantity inline
- [ ] Optimistic updates with error rollback
- [ ] POST/DELETE to `/api/lists/:id/items`

---

#### TICKET 10.3 — Multiple Lists Support
**Type:** Feature | **Priority:** P1 | **Owner:** Web Team

**Description**  
Support creating and switching between multiple shopping lists.

**Criterios de aceptación**
- [ ] List selector dropdown
- [ ] Create new list dialog
- [ ] Rename/delete lists
- [ ] Set active list
- [ ] Persist active list in localStorage

---

### Epic 11 — Real-Time Price Comparison
**Priority:** P0  
**Goal:** Dynamic store comparison using real API data

#### TICKET 11.1 — Live Store Comparison
**Type:** Feature | **Priority:** P0 | **Owner:** Web Team

**Description**  
Calculate and display total cost per store for the active shopping list.

**Criterios de aceptación**
- [ ] Fetch totals from `/api/list-totals` endpoint
- [ ] Display stores sorted by cheapest first
- [ ] Show total price per store
- [ ] Show savings vs most expensive option
- [ ] Highlight best option with badge
- [ ] Loading skeleton while calculating

---

#### TICKET 11.2 — Price Breakdown by Item
**Type:** Feature | **Priority:** P1 | **Owner:** Web Team

**Description**  
Show per-item pricing across stores in a comparison table.

**Criterios de aceptación**
- [ ] Expandable table showing item × store grid
- [ ] Highlight cheapest price per item
- [ ] Show "not available" for missing products
- [ ] Mobile-optimized view (stacked cards)

---

#### TICKET 11.3 — Store Filtering
**Type:** Feature | **Priority:** P1 | **Owner:** Web Team

**Description**  
Allow users to filter which stores appear in comparison.

**Criterios de aceptación**
- [ ] Checkbox list of available stores
- [ ] Save preferences in localStorage
- [ ] Update comparison when filters change
- [ ] Show count of active stores

---

### Epic 12 — User Preferences & Settings
**Priority:** P1  
**Goal:** Personalization matching Android app settings

#### TICKET 12.1 — Settings Page
**Type:** Feature | **Priority:** P1 | **Owner:** Web Team

**Description**  
Create a settings page mirroring Android's SettingsScreen functionality.

**Sections:**
- Preferencias de compra (zona, tiendas activas)
- Alertas y precios (configuración de notificaciones)
- Plan (Free/Pro status)
- Acerca de (versión, soporte)

**Criterios de aceptación**
- [ ] Settings page with navegable sections
- [ ] Zone selector (dropdown)
- [ ] Store toggles (enable/disable)
- [ ] Alert frequency selector
- [ ] Save preferences to backend or localStorage
- [ ] Match Android UI design

---

#### TICKET 12.2 — Zone/Location Selection
**Type:** Feature | **Priority:** P1 | **Owner:** Web Team

**Description**  
Allow users to select their zone to get location-specific pricing.

**Criterios de aceptación**
- [ ] Zone selector with popular zones
- [ ] Map-based zone picker (optional)
- [ ] Persist selection in user preferences
- [ ] Filter stores by selected zone
- [ ] Show zone name in header/settings

---

#### TICKET 12.3 — Theme Toggle (Dark/Light)
**Type:** Feature | **Priority:** P2 | **Owner:** Web Team

**Description**  
Add dark/light theme toggle matching Android's Material 3 themes.

**Criterios de aceptación**
- [ ] Toggle in settings or header
- [ ] Persist preference in localStorage
- [ ] Smooth transition between themes
- [ ] All components support both themes
- [ ] Match Android color schemes

---

### Epic 13 — Responsive Design & PWA
**Priority:** P1  
**Goal:** Make web app mobile-friendly and installable

#### TICKET 13.1 — Mobile-First Responsive Design
**Type:** Task | **Priority:** P1 | **Owner:** Web Team

**Description**  
Ensure all pages work perfectly on mobile devices.

**Breakpoints:**
- Mobile: < 640px
- Tablet: 640px - 1024px
- Desktop: > 1024px

**Criterios de aceptación**
- [ ] All pages tested on mobile viewport
- [ ] Bottom navigation on mobile
- [ ] Sidebar navigation on desktop
- [ ] Touch-friendly tap targets (min 44px)
- [ ] No horizontal scrolling
- [ ] Readable text sizes on all devices

---

#### TICKET 13.2 — Progressive Web App (PWA)
**Type:** Feature | **Priority:** P1 | **Owner:** Web Team

**Description**  
Make the web app installable as a PWA for better mobile experience.

**Criterios de aceptación**
- [ ] `manifest.json` with app metadata
- [ ] Service worker for offline support
- [ ] Install prompt on mobile browsers
- [ ] App icons (192x192, 512x512)
- [ ] Splash screen
- [ ] Offline page showing cached data

---

#### TICKET 13.3 — Performance Optimization
**Type:** Task | **Priority:** P2 | **Owner:** Web Team

**Description**  
Optimize web app for fast loading and smooth interactions.

**Criterios de aceptación**
- [ ] Code splitting by route
- [ ] Lazy loading for images
- [ ] Memoization for expensive calculations
- [ ] Lighthouse score > 90
- [ ] First Contentful Paint < 1.5s
- [ ] Time to Interactive < 3s

---

## 🎨 Epic 14 — Enhanced UX Features
**Priority:** P2  
**Goal:** Delight users with polished interactions

#### TICKET 14.1 — Animations & Transitions
**Type:** Enhancement | **Priority:** P2 | **Owner:** Web Team

**Description**  
Add smooth animations matching Android's motion design.

**Criterios de aceptación**
- [ ] Page transitions (fade/slide)
- [ ] List item animations (add/remove)
- [ ] Loading skeletons instead of spinners
- [ ] Micro-interactions (button press, hover states)
- [ ] Respect `prefers-reduced-motion`

---

#### TICKET 14.2 — Price History Chart
**Type:** Feature | **Priority:** P2 | **Owner:** Web Team

**Description**  
Show price trends over time for products (matching Android TICKET 2.3).

**Criterios de aceptación**
- [ ] Line chart using Chart.js or Recharts
- [ ] Show last 7/30/90 days
- [ ] Highlight lowest price point
- [ ] Mobile-optimized chart
- [ ] Export data as CSV

---

#### TICKET 14.3 — Notifications/Alerts UI
**Type:** Feature | **Priority:** P2 | **Owner:** Web Team

**Description**  
Display price drop notifications in the web app.

**Criterios de aceptación**
- [ ] Notification bell icon with badge count
- [ ] Notification panel with recent alerts
- [ ] Mark as read functionality
- [ ] Link to product from notification
- [ ] Push notification permission prompt (optional)

---

## 📊 Summary

**Total Proposed Tickets:** 21  
**Epics:** 6 (Epic 9-14)

**Priority Breakdown:**
- P0 (Critical): 9 tickets
- P1 (High): 9 tickets  
- P2 (Medium): 3 tickets

**Estimated Effort:** 3-4 weeks for P0 + P1 tickets with 1 developer

**Dependencies:**
- Epic 9 → All other epics (foundation)
- Epic 10 → Epic 11 (need lists to compare)
- Epic 12 → Can be parallel
- Epic 13-14 → After core features (P0/P1)
