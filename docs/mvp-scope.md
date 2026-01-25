# MVP scope

## Status
- Drafted. Needs validation with stakeholders and KPIs.

## P0 (MVP)
- Shopping list CRUD (name, qty, unit)
- Compare totals by store (cheapest first)
- Price history (min 7 days)
- Local price drop alerts (WorkManager)
- Ads: banner + rewarded (no interstitials)
- Pro: no ads

## P1
- Barcode scan
- Substitutes (same size/brand)
- Unit normalization (g/ml/kg)
- Multi-zone + favorite stores
- Cheapest store alerts

## P2
- Receipt OCR
- Family collaboration
- Shopping route (split list across stores)
- Affiliates / cashback

## Out of scope for MVP
- Full backend ingestion pipeline
- FCM push notifications
- Rust fuzzy matching (optional later)
- Full catalog scraping

## Target users
- LATAM shoppers comparing 2–4 nearby stores
- Price-sensitive households

## Free vs Pro
**Free**
- All core list + compare features
- Banner ads on passive screens
- Rewarded ads for temporary unlocks

**Pro**
- No ads
- Full price history
- Multi-store compare (no time limits)

## Notes
- Offline-first: local DB is source of truth
- Only metadata + price snapshots stored
