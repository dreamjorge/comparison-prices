# QA Test Plan — Phase 1 MVP

This document defines a functional QA pass for the Phase 1 (P0) MVP scope.
It focuses on the offline-first Android experience and verifies the core flows
listed in `docs/mvp-scope.md` and `docs/kickoff.md`.

## Goals
- Validate the main user flows end-to-end without crashes.
- Confirm pricing logic, history display, and alert behavior.
- Ensure ads + Pro gating behave as expected.

## Test environment
- **Device**: Android 13+ emulator + one physical device if available.
- **Build**: Debug build with mock demo data seeded.
- **Network**: Offline and online modes (toggle airplane mode).

## Pre-flight checks
1. Fresh install (clear app data).
2. Launch the app and confirm the demo list is seeded once.
3. Verify system notification permissions are requested on Android 13+.

## Test cases

### 1) Shopping list CRUD (TICKET 2.1)
- Create a new list item using the add dialog.
- Edit quantity + unit with +/- controls.
- Delete an item and confirm removal.
- Relaunch the app and ensure persistence.

### 2) Compare totals by store (TICKET 2.2)
- Open Compare and confirm stores sorted cheapest-first.
- Validate the cheapest store is highlighted.
- Confirm totals reflect list item quantities.
- Verify the savings vs next store is shown.

### 3) Price history (TICKET 2.3)
- Open a product detail and verify 7-day history data appears.
- Confirm chart renders without visual glitches.
- Switch to airplane mode and re-open to validate offline access.

### 4) Alerts for price drops (TICKET 5.1)
- Trigger the demo notification flow (PriceRefreshWorker).
- Confirm notification text and tap action.
- Disable notifications and confirm app handles the permission state.

### 5) Ads: banner + rewarded (TICKET 6.1 + 6.2)
- Confirm banner ad renders on Home/Compare screens.
- Open Paywall and trigger the rewarded ad button.
- Verify the temporary Pro unlock state updates in UI.
- Validate the rewarded unlock expires after the configured window.

### 6) Pro tier (TICKET 6.3)
- Toggle paid state (mock purchase) and confirm ads are hidden.
- Verify Pro gating is lifted for premium areas.

## Regression checklist
- Switch tabs quickly to ensure no duplicated demo data is inserted.
- Rotate device and verify Compose state is restored.
- Validate no crashes when app resumes after backgrounding.

## Exit criteria
- No crashes in any flow above.
- Main flows pass on emulator and at least one physical device.
- Any blockers are recorded with reproduction steps + screenshots.
