# 🧭 Tasks de desarrollo

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
- [x] TICKET 1.3 — Seeding demo data idempotente en ViewModels.
- [x] TICKET 2.2 — Comparador de precios por tienda.
- [x] TICKET 2.4 — Encabezado dinámico y ahorro por tienda en comparador.
- [x] TICKET 8.3 — Ajustar totales del comparador según cantidades de la lista.

## Riesgos y mitigaciones
- **Riesgo:** migrar `brand` nulo a cadena vacía puede generar inconsistencias si alguna capa espera `NULL`.
  **Mitigación:** normalizar `brand` en el seeding demo y tratar `""` como “sin marca” en UI/consultas futuras.
- **Riesgo:** deduplicar `list_items` después del remap de `productId` puede eliminar cantidades duplicadas si existían items idénticos.
  **Mitigación:** agregar una tarea de seguimiento para evaluar si conviene fusionar cantidades en migraciones futuras o ajustar la lógica de inserción.
- **Riesgo:** los contratos pueden desincronizarse si se edita `openapi.json` sin regenerar tipos.
  **Mitigación:** mantener un test que valide que `src/generated.ts` coincide con la salida del generador.

## Backlog (seguimiento)
- [ ] TICKET 9.6 — Definir checklist CI para verificar generación de contratos desde `packages/contracts` y detectar divergencias.
- [ ] TICKET 9.7 — Conectar el scaffold web con contratos reales y datos de API cuando estén disponibles.
- [ ] TICKET 9.8 — Integrar `@comparison-prices/contracts` en el frontend web para tipar las llamadas HTTP.
