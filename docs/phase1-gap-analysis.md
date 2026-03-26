# Phase 1 MVP — Gap Analysis

This document audits Phase 1 (MVP P0) against the agreed scope and backlog references. It is based on:
- `docs/mvp-scope.md` (MVP P0 definition)
- `docs/kickoff.md` (Week 1 foundations)
- `tasks.md` / `tickets.md` (ticket status)

## Phase 1 scope (P0)
From `docs/mvp-scope.md`, Phase 1 requires:
1. Shopping list CRUD (name, qty, unit)
2. Compare totals by store (cheapest first)
3. Price history (min 7 days)
4. Local price drop alerts (WorkManager)
5. Ads: banner + rewarded (no interstitials)
6. Pro tier (no ads)

## Current status summary
**Already implemented (per `tasks.md` / app code):**
- Shopping list CRUD (TICKET 2.1)
- Compare totals by store (TICKET 2.2)
- Price history (TICKET 2.3)
- Local price drop alerts (TICKET 5.1)
- Banner ads (TICKET 6.1)
- Pro tier (TICKET 6.2)

**Missing or incomplete for Phase 1:**
1. **Rewarded ads for temporary unlocks (P0)**  
   Scope requires banner + rewarded ads. Banner ads are done, but rewarded ads were not yet wired to unlock access.
2. **QA + release readiness (P0 execution gap)**  
   Tickets 7.1–7.3 (QA pass, Play Store checklist, AAB build) are still pending. These are not code blockers but are still required for shipping Phase 1 to beta.
3. **Mock dataset documentation (Week 1 foundation gap)**  
   `docs/kickoff.md` calls for mock data documentation; there is schema in `docs/data-schema.md` and dedup strategy in `docs/demo-data-dedup.md`, but a focused mock-data description is missing.

## Actions taken in this iteration
1. **Rewarded ads implementation (P0 requirement).**
   - Implemented rewarded ad button in the paywall and wired it to a temporary Pro unlock flow.
   - Added state for rewarded unlock expiration so the UI can show when temporary access ends.
   - Added unit tests for the premium/rewarded state logic.
2. **Mock dataset documentation.**
   - Added `docs/mock-data.md` to describe current demo data sources and how they are used.

## Remaining Phase 1 gaps (next steps)
1. **QA + Release pipeline (TICKETS 7.1–7.3).**
   - Define a QA test plan and complete a smoke run for the main flows.
   - Create a Play Store Data Safety document and generate a beta AAB.
2. **Rewarded expiration refresh.**
   - The rewarded unlock currently relies on local device time; add periodic refresh or analytics to validate expiration and usage.
