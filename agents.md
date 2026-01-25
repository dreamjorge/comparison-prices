# Plan de desarrollo (multi-agent) — Android App: Comparador de precios por lista de súper (LATAM)

## Status update (2026-01-25)
- Agent 1 outputs drafted: `docs/mvp-scope.md`, `docs/wireframes/ux-flow.md`, `docs/wireframes/notes.md`
- Agent 2 started: Android scaffold in `app-android/` (needs build validation + feature work)
- Agents 3–6 pending

## Objetivo del MVP
- El usuario crea una **lista de productos** (texto o escaneo de código de barras).
- La app calcula **total por tienda** y muestra **dónde sale más barato**.
- Guarda **historial de precios** y envía **alertas** (baja de precio / mejor tienda).
- Monetización: **Free (banners + rewarded)** y **Pro (sin ads + features)**.

---

## Arquitectura recomendada
- **Android (Kotlin)** + Jetpack (Compose, Room, WorkManager, Hilt)
- **DB local**: Room/SQLite (offline-first)
- **Backend mínimo** (opcional MVP): FastAPI (Python) para ingesta/normalización de precios
- **Motor de matching** (opcional): Rust vía JNI/FFI para fuzzy-match y normalización avanzada
- **Push/alerts**: Local notifications (MVP). FCM solo si hay backend.

---

## Roles de Agents (equipo ideal de 6)
> Puedes correrlos como “agentes” en paralelo o como checklist secuencial.

### Agent 1 — Product/UX (PM + Diseño)
**Entregables**
- User stories + flujo MVP
- Wireframes (4–6 pantallas)
- Reglas de UX para ads
- Copy (propuesta de valor, onboarding, paywall)

**Definiciones clave**
- “Lista” = items (nombre, cantidad, unidad preferida)
- “Tienda” = conjunto de fuentes con cobertura por zona

**Done**
- Documento con flujo completo + pantallas + eventos de analítica

---

### Agent 2 — Android Core (App + DB)
**Entregables**
- Proyecto Android base (Compose, Room, Hilt)
- Modelos + repositorios + casos de uso
- Pantallas:
  - Home (lista actual)
  - Comparador (totales por tienda)
  - Detalle de producto (historial + sustitutos)
  - Configuración (zona/tiendas)
- WorkManager para refresh de precios y alertas

**Done**
- App funcional offline con datos mock + DB persistente

---

### Agent 3 — Data/Ingesta (Python)
**Entregables**
- Normalizador de productos (marca, tamaño, unidades)
- Pipeline de ingesta por fuente:
  - API/feeds permisivos
  - Crowdsourcing: precios manuales / ticket OCR (fase 2)
- API mínima (si aplica):
  - `GET /stores`
  - `GET /search?q=...`
  - `POST /price_snapshots` (para actualizar precios)
  - `GET /price_history?product_id=...`

**Done**
- Dataset de prueba + API estable + esquema de datos

---

### Agent 4 — Matching/Ranking (Rust opcional)
**Entregables**
- Librería Rust con:
  - Normalización de strings (acentos, stopwords)
  - Fuzzy match (Jaro-Winkler / Levenshtein)
  - Scoring de equivalencias (mismo producto vs sustituto)
- Export JNI:
  - `matchCandidates(query, candidates) -> ranked list`

**Done**
- Pruebas unitarias con casos LATAM (marcas y tamaños)

---

### Agent 5 — Monetización/Ads
**Entregables**
- Integración AdMob:
  - Banner (solo pantallas pasivas)
  - Native ads (tarjeta entre items) (opcional MVP)
  - Rewarded (desbloquea historial > 7 días o multi-tienda 24h)
- Paywall Pro (Google Play Billing)
- Reglas anti-UX-killer (no interstitials en acciones clave)

**Done**
- Free con ads + Pro sin ads funcionando en release build

---

### Agent 6 — QA/Release (Play Store)
**Entregables**
- Plan de pruebas (smoke + regresión)
- Crash reporting + analytics (Firebase opcional)
- Checklist Play Store:
  - Data safety (qué guardas, qué envías)
  - Permisos mínimos
  - Políticas de anuncios (etiquetado “Anuncio”)
- CI básico (GitHub Actions):
  - lint + unit tests + build APK/AAB

**Done**
- AAB listo + listing + screenshots + beta interna

---

## Milestones (4 semanas)
### Semana 1 — Fundaciones + UX
- Agent 1: flujo + wireframes + alcance MVP
- Agent 2: app base + navegación + Room
- Agent 3: dataset mock + esquema data

**Salida**: app navegable con datos mock + DB

### Semana 2 — Comparador + Historial
- Agent 2: lógica de lista → totales por tienda
- Agent 3: normalización base + endpoints (si hay backend)
- Agent 4: fuzzy match (si se usa)

**Salida**: compara “mi lista” en 2–3 tiendas con historial

### Semana 3 — Alertas + Ads + Pro
- Agent 2: WorkManager + alertas locales
- Agent 5: banners + rewarded + paywall

**Salida**: Free monetizable + Pro funcional

### Semana 4 — Pulido + Release
- Agent 6: QA + políticas + AAB + listing
- Agent 1: copy final + onboarding + pricing

**Salida**: Beta cerrada / producción

---

## Backlog (prioridad alta → baja)
### P0 (MVP)
- Lista (crear/editar, cantidades)
- Comparador total por tienda
- Historial simple
- Alertas básicas
- Ads banner + rewarded
- Pro sin ads

### P1
- Escaneo de código de barras
- Sustitutos equivalentes (mismo tamaño/marca)
- Normalización de unidades (g/ml/kg)
- Multi-zona / sucursales favoritas

### P2
- OCR de tickets
- Colaboración familiar
- “Ruta de compra” (dividir lista entre 2 tiendas)
- Afiliados / cashback

---

## Reglas de datos (para evitar problemas)
- Guardar **solo metadata** y snapshots de precio
- No copiar catálogos completos si la fuente no lo permite
- Cache + rate limit
- Siempre link a fuente (si aplica)

---

## Estructura de repos recomendada
- `app-android/`
- `api-python/` (opcional)
- `matching-rust/` (opcional)
- `docs/` (wireframes, decisiones, políticas)

---

## Prompts listos para cada Agent
### Agent 1 (UX)
- “Diseña el flujo MVP y wireframes para: lista → comparador → detalle → settings → paywall. Incluye reglas de ads y eventos de analítica.”

### Agent 2 (Android)
- “Implementa Compose + Room + WorkManager. Modela Product, Store, PriceSnapshot, ListItem. Construye pantallas y casos de uso para comparar totales.”

### Agent 3 (Data)
- “Define el esquema de datos y una API mínima. Crea normalización (unidades, tamaños, marcas) y un dataset mock con 200 productos y 3 tiendas.”

### Agent 4 (Rust)
- “Crea una librería de fuzzy matching para nombres de productos en español y tamaños. Devuelve top-N candidatos con score.”

### Agent 5 (Monetización)
- “Integra AdMob banner + rewarded sin interstitials. Implementa paywall Pro con Billing. Define qué desbloquea rewarded vs Pro.”

### Agent 6 (QA/Release)
- “Crea checklist de QA + Play Store Data Safety + política de ads. Configura CI para build AAB y pruebas unitarias.”

---
