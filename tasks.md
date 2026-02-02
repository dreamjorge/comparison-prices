# 🧭 Tasks de desarrollo

> **📊 Phase 1 Audit Available:** See `phase1_audit.md` in artifacts for complete status analysis (44% complete - 7.5/17 tickets)

## 🚨 Crítico - Bloqueadores de MVP

### ✅ TICKET 2.1 — Crear y editar lista de compras (CRUD operations)
**Status:** ✅ COMPLETE | **Priority:** P0 | **Effort:** 4 days | **Completed:** 2026-02-02

**Implementado:**
- ✅ Implementar diálogo "Agregar Producto" en HomeScreen
- ✅ Búsqueda de productos (autocomplete)
- ✅ Selector de cantidad + unidad
- ✅ Botón delete funcional con confirmación
- ✅ Controles +/- para cantidad

---

### ✅ TICKET 5.1 — Alertas locales de precios
**Status:** ✅ COMPLETE | **Priority:** P0 | **Effort:** 3 days | **Completed:** 2026-02-02

**Implementado:**
- ✅ Created NotificationHelper.kt with Hilt injection
- ✅ Notification channel creation in Application.onCreate()
- ✅ Android 13+ permission handling (POST_NOTIFICATIONS)
- ✅ Integrated NotificationHelper with PriceRefreshWorker
- ✅ Demo price drop notifications working

---

### TICKET 6.1 — Integración AdMob (Banners)
**Status:** ❌ NOT STARTED | **Priority:** P0 | **Effort:** 2 days

**Problema:** Sin monetización = sin revenue.

**Tareas:**
- [ ] Agregar AdMob SDK a build.gradle
- [ ] Crear AdBanner composable
- [ ] Obtener ad unit IDs (test + production)
- [ ] Integrar banners en Home (bottom) y Compare (entre stores)
- [ ] Manejar ad load errors gracefully

---

### TICKET 6.2 — Paywall & Pro tier
**Status:** ❌ NOT STARTED | **Priority:** P0 | **Effort:** 3 days

**Problema:** No hay upsell a Pro, no hay billing.

**Tareas:**
- [ ] Crear PaywallScreen.kt
- [ ] Crear PremiumViewModel.kt con isPro state
- [ ] Integrar Google Play Billing Library
- [ ] Definir beneficios Pro (sin ads + historial extendido)
- [ ] Conditionally mostrar/ocultar ads basado en isPro

---

## Seleccionado para esta iteración
- [x] TICKET 8.6 — Normalizar brand nulo en productos demo.
- [x] TICKET 9.1 — Documentar estructura monorepo web + backend compartido.
- [x] TICKET 9.2 — Scaffold de frontend web base (app-web/ o web/) con routing, layout y configuración básica.
- [x] TICKET 9.3 — Definir contratos compartidos (OpenAPI/DTOs) en packages/contracts con generación de tipos.
- [ ] TICKET 9.4 — Implementar endpoints mínimos para web (stores, search, list totals, price history) y ajustes de API.
- [ ] TICKET 9.5 — Configurar CORS, autenticación y paginado (cursor/limit) para consumo web seguro.

## Completados
- [x] TICKET 0.1 — Definir alcance del MVP.
- [x] TICKET 0.2 — Wireframes y flujo UX.
- [x] TICKET 1.1 — Crear proyecto Android base.
- [x] TICKET 1.2 — Modelos de datos locales.
- [x] TICKET 1.3 — Seeding demo data idempotente en ViewModels. ⚠️ **Race condition conocida**
- [x] TICKET 2.1 — Crear y editar lista de compras (CRUD operations). ✨ **2026-02-02**
- [x] TICKET 2.2 — Comparador de precios por tienda.
- [x] TICKET 2.4 — Encabezado dinámico y ahorro por tienda en comparador.
- [x] TICKET 5.1 — Alertas locales de precios (notifications completas). ✨ **2026-02-02**
- [x] TICKET 8.3 — Ajustar totales del comparador según cantidades de la lista.

## Riesgos y mitigaciones
- **Riesgo:** Race condition en seeding (TICKET 1.3) - Home y Compare ViewModels pueden seed concurrently.
  **Mitigación:** Centralizar seeding en Application.onCreate() o usar synchronization.
- **Riesgo:** migrar `brand` nulo a cadena vacía puede generar inconsistencias si alguna capa espera `NULL`.
  **Mitigación:** normalizar `brand` en el seeding demo y tratar `""` como "sin marca" en UI/consultas futuras.
- **Riesgo:** deduplicar `list_items` después del remap de `productId` puede eliminar cantidades duplicadas si existían items idénticos.
  **Mitigación:** agregar una tarea de seguimiento para evaluar si conviene fusionar cantidades en migraciones futuras o ajustar la lógica de inserción.
- **Riesgo:** los contratos pueden desincronizarse si se edita `openapi.json` sin regenerar tipos.
  **Mitigación:** mantener un test que valide que `src/generated.ts` coincide con la salida del generador.
- **⚠️ NUEVO RIESGO:** App no es usable sin TICKET 2.1 - usuarios no pueden crear listas.
  **Mitigación:** Priorizar TICKET 2.1 antes que features avanzadas.

## Backlog (seguimiento)
- [ ] TICKET 9.6 — Definir checklist CI para verificar generación de contratos desde `packages/contracts` y detectar divergencias.
- [ ] TICKET 9.7 — Conectar el scaffold web con contratos reales y datos de API cuando estén disponibles.
- [ ] TICKET 9.8 — Integrar `@comparison-prices/contracts` en el frontend web para tipar las llamadas HTTP.
- [ ] TICKET 5.2 — Alertas de "lista más barata" (cambio de tienda óptima).
- [ ] TICKET 6.3 — Rewarded ads (desbloquear features temporalmente).
- [ ] TICKET 7.1 — Aumentar cobertura de tests a 80%+.
- [ ] TICKET 7.2 — CI/CD para Play Store (AAB build + lint + tests).
- [ ] TICKET 7.3 — Documentar Data Safety para Play Store listing.
