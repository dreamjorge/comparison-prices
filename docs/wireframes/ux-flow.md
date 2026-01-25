# UX flow (MVP)

## Status
- Drafted (text-only). Needs low-fidelity wireframe export to PDF/images.

## Screens
1) Home (Shopping list)
2) Compare (Totals by store)
3) Product detail (History + substitutes)
4) Settings (Zone + stores)
5) Paywall (Free vs Pro)

## Primary flow
- Open app -> Home (list)
- Add item -> appears in list
- Tap Compare -> totals by store -> cheapest first
- Tap a product -> Product detail (history)
- Settings -> set zone, choose stores
- Paywall -> upgrade to Pro

## Secondary flows
- Empty state -> CTA to add items
- Price drop alert -> opens Product detail
- Cheapest store alert -> opens Compare

## Navigation
- Bottom bar: Home, Compare, Settings
- Modal/Bottom sheet: Add item, Edit item
- Paywall via Settings and feature gates

## Ad rules (MVP)
- Banner only on passive screens: Home, Compare, Settings
- No ads on Add/Edit flow
- Rewarded ads only when user explicitly taps
