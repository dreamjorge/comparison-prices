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

### ✅ TICKET 1.3 — Seeding demo data idempotente
**Status:** ✅ COMPLETE | **Priority:** P1 | **Effort:** 1 day | **Completed:** 2026-02-02
**Fix:** Movido a Application.onCreate() para evitar race conditions.

---

### ✅ TICKET 6.1 — Integración AdMob (Banners)
**Status:** ✅ COMPLETE | **Priority:** P0 | **Effort:** 2 days | **Completed:** 2026-02-02
**Implementado:** AdBanner component, integration in Home/Compare, Test IDs.

---

### ✅ TICKET 6.2 — Paywall & Pro tier
**Status:** ✅ COMPLETE | **Priority:** P0 | **Effort:** 3 days | **Completed:** 2026-02-02
**Implementado:** DataStore for state, PremiumViewModel, PaywallScreen UI.

---

## Seleccionado para esta iteración
- [x] TICKET 8.6 — Normalizar brand nulo en productos demo.
- [x] TICKET 9.1 — Documentar estructura monorepo web + backend compartido.
- [x] TICKET 9.2 — Scaffold de frontend web base (app-web/ o web/) con routing, layout y configuración básica.
- [x] TICKET 9.3 — Definir contratos compartidos (OpenAPI/DTOs) en packages/contracts con generación de tipos.
- [x] TICKET 9.7 — Conectar el scaffold web con contratos reales y datos de API cuando estén disponibles. ✅ **Done: API integration in pages + live fetching**
- [x] TICKET 9.8 — Integrar `@comparison-prices/contracts` en el frontend web para tipar las llamadas HTTP. ✅ **Done: Shared types.ts and typed fetch wrapper**
- [x] TICKET 9.9 — Estabilización post-merge de Android (resolución de conflictos KSP/Hilt). ✅ **Done: Resolved 12+ files with conflict markers**

## Completados
- [x] TICKET 0.1 — Definir alcance del MVP.
- [x] TICKET 0.2 — Wireframes y flujo UX.
- [x] TICKET 1.1 — Crear proyecto Android base.
- [x] TICKET 1.2 — Modelos de datos locales.
- [x] TICKET 1.3 — Seeding demo data idempotente. ✅ **Race condition fixed**
- [x] TICKET 2.1 — Crear y editar lista de compras (CRUD operations). ✨ **2026-02-02**
- [x] TICKET 2.2 — Comparador de precios por tienda.
- [x] TICKET 2.3 — Historial de precios con gráficas. ✨ **2026-02-02**
- [x] TICKET 2.4 — Encabezado dinámico y ahorro por tienda en comparador.
- [x] TICKET 5.1 — Alertas locales de precios (notifications completas). ✨ **2026-02-02**
- [x] TICKET 6.1 — Integración AdMob (Banners). ✨ **2026-02-02**
- [x] TICKET 6.2 — Paywall & Pro tier (mocked billing). ✨ **2026-02-02**
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
- **⚠️ NUEVO RIESGO:** El desbloqueo temporal por rewarded depende del reloj local del dispositivo.
  **Mitigación:** limpiar expiraciones al iniciar la app y planear un refresh periódico/validación de tiempo en una tarea futura.

## Backlog (seguimiento)
- [x] TICKET 9.6 — Definir checklist CI para verificar generación de contratos desde `packages/contracts` y detectar divergencias. ✅ **Done: scripts/verify.sh + CI Job**
- [x] TICKET 5.2 — Alertas de "lista más barata" (cambio de tienda óptima). ✅ **Done: PriceRefreshWorker compara tienda más barata y notifica si cambia**
- [x] TICKET 6.3 — Rewarded ads (desbloquear features temporalmente). ✅ **Done: RewardedAdButton ya integrado con unlockRewardedAccess()**
- [x] TICKET 7.1 — Aumentar cobertura de tests a 80%+. ✅ **Done: +9 tests en PriceRefreshWorkerLogicTest (~26 total)**
- [x] TICKET 7.2 — CI/CD para Play Store (AAB build + lint + tests). ✅ **Done: bundleDebug en run-ci-local.sh y AAB artifact en ci.yml**
- [x] TICKET 7.3 — Documentar Data Safety para Play Store listing. ✅ **Done: docs/data-safety.md**
- [x] TICKET 7.4 — Refrescar expiración de Pro temporal por rewarded (ticker en background + analítica de uso). ✅ **Done: clearExpiredRewards() en doWork()**
- [ ] TICKET 7.5 — Automatizar ejecución del plan de QA funcional con matriz de dispositivos.
- [x] TICKET 8.7 — Normalizar brand vacío en UI y repositorios. ✅ **Done: BrandUtils.kt + displayBrand() aplicado en HomeScreen**
- [x] TICKET 8.8 — Revisar merge de cantidades en deduplicación. ✅ **Done: decisión MIN(id) "first-item wins" documentada en tickets.md**
- [~] TICKET 11.3 — Integración compliant de fuentes externas + link-out Google Shopping (en progreso: API + contratos + UI listos; faltan pruebas end-to-end).
- [ ] TICKET 11.4 — Hardening de pruebas y rollout para integración compliant.
- [ ] TICKET 11.5 — Selección e integración de APIs legales para México (roadmap proveedores).
