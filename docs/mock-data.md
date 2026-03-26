# Mock Data Overview

This document describes the mock data currently used in Phase 1 so local development can proceed without a real backend.

## Android demo data (offline-first)
The Android app seeds local demo data on first launch, including:
- A demo shopping list and list items.
- Products with normalized brands and default units.
- Price snapshots to power the compare and history screens.

This seeding happens in the Android app layer to keep the MVP usable offline while the real data pipeline is still pending.

## API mock data (optional)
The Python FastAPI service (`api-python/`) includes mock product lists, store data, and price history generation for local testing. It is intended as a placeholder until a real ingestion pipeline is available.

## Recommended usage
- **Mobile-first MVP:** rely on Android demo data to keep the app fully offline.
- **API testing:** use `api-python` mocks to validate basic list/search flows before any real sources are connected.

## Next steps
- Replace the mock data pipeline with approved data sources once ingestion work starts (Phase 2).
- Keep mock datasets consistent with `docs/data-schema.md` to avoid contract drift.
