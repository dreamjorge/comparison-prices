# Project kickoff (from agents.md + tickets.md)

## Goal
Build MVP for a LATAM grocery list price comparator app.

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