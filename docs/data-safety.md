# Data Safety Summary — Phase 1 MVP

This document maps the data collected or stored by the Phase 1 MVP to the
Google Play Data Safety section. The MVP is offline-first and does not require
backend accounts or cloud sync.

## Data collected
The MVP does **not** collect user data on a server. All data is stored locally
on the device.

## Data stored on device
| Data type | Purpose | Storage | Notes |
| --- | --- | --- | --- |
| Shopping lists (items, qty, unit) | Core list + comparison | Room (local DB) | Offline-only in Phase 1. |
| Price snapshots | Price history display | Room (local DB) | Used for charts + alerts. |
| User preferences (Pro/rewarded state) | Feature gating | DataStore / SharedPreferences | No account required. |
| Notification permissions | Alerting | OS permission state | Used to enable price drop alerts. |

## Data sharing
- **No data sharing** with third parties in Phase 1.
- Ads (AdMob) are shown, but the MVP does not transmit user-generated content.

## Data deletion
- Clearing app data removes all local lists, history, and preferences.
- Uninstalling the app deletes all stored data.

## Security practices
- Data stored locally using Android Jetpack libraries.
- No external storage permissions required.

## Future considerations (Phase 2+)
If cloud sync or account login is introduced, this document must be updated to
reflect:
- Account identifiers (email, UID).
- Sync of shopping lists to backend storage.
- Analytics / crash reporting collection.
