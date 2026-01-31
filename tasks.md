# 🧭 Tasks de desarrollo

## Seleccionado para esta iteración
- [x] TICKET 8.6 — Normalizar brand nulo en productos demo.

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
