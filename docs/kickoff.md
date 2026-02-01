# Project kickoff (from agents.md + tickets.md)

## Goal
Build MVP for a LATAM grocery list price comparator app.

## Current status (2026-01-25)
- UX flow + wireframe notes drafted
- MVP scope drafted
- Android base scaffold created (Compose + Room + Hilt + WorkManager + Navigation)
- Gradle wrapper jar missing (needs generation/download)

## MVP scope (P0)
- Shopping list CRUD (name, qty, unit)
- Compare totals by store, cheapest first
- Price history (min 7 days)
- Local alerts for price drops
- Ads: banner + rewarded
- Pro: no ads

## Week 1 focus (foundation + UX)
- Agent 1: define MVP scope doc + wireframes
- Agent 2: Android base project + navigation + Room
- Agent 3: mock dataset schema + sample data

## Ticket order (recommended)
1) TICKET 0.1: define MVP scope
2) TICKET 0.2: wireframes + UX flow
3) TICKET 1.1: create Android base project
4) TICKET 1.2: local data models (Room)
5) TICKET 3.1: mock dataset

## Outputs to produce first
- docs/mvp-scope.md
- docs/wireframes/ (exported images or PDF)
- app-android/ (base project compiles)
- docs/data-schema.md + docs/mock-data.md

## Notes
- Keep offline-first. No backend required for MVP.
- Use only metadata + price snapshots.
- Avoid copying full catalogs from sources.

## Web + backend compartido (nuevo)
- Propuesta de monorepo con `app-web/`, `api/` y `packages/contracts` para contratos compartidos y generación de tipos.
- Ver detalle en `docs/monorepo-structure.md`.

## Next steps (pick up)
1) Generate Gradle wrapper jar and verify `./gradlew :app:tasks` runs
2) Implement TICKET 1.2: Room models/DAOs migrations + basic repository layer
3) Implement TICKET 2.1: shopping list CRUD UI + local persistence
4) Create mock dataset docs + loader (TICKET 3.1)
