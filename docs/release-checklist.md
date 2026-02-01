# Play Store Release Checklist

Follow this checklist to ensure the application is ready for the Google Play Store.

## Metadata & Assets
- [ ] **App Title**: "Compara Precios" (Check character limits).
- [ ] **Short Description**: "Ahorra en tus compras comparando precios en tiempo real."
- [ ] **Full Description**: Detailed explanation of features (Price History, Store Totals, Lists).
- [ ] **Feature Graphic**: 1024x500 PNG/JPG.
- [ ] **App Icon**: 512x512 PNG.
- [ ] **Screenshots**: At least 4 per device type (Phone, 7" Tablet, 10" Tablet).

## Technical Requirements
- [ ] **Package Name**: `com.compareprices`.
- [ ] **Version Code**: Increment on every release.
- [ ] **Target SDK**: Ensure it meets Play Store minimum (currently 34-35+).
- [ ] **App Bundle (AAB)**: Generated and signed.

## Privacy & Safety
- [ ] **Privacy Policy**: Hosted URL required.
- [ ] **Data Safety Section**: Map all collected data (UserPrefs, etc. although mostly local).
- [ ] **Ad Declaration**: Declare that the app contains ads.

## Final QA Pass
- [ ] End-to-end shopping list flow.
- [ ] Price history rendering.
- [ ] Background alerts (WorkManager).
- [ ] AdMob test units replaced with production units (CAUTION).
