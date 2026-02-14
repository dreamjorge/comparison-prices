# Handoff: TICKET 11.3 — Integracion compliant de fuentes externas + link-out Google Shopping

## Estado actual
- `api/` ya tiene capa de proveedores mock en:
  - `api/src/providers/types.ts`
  - `api/src/providers/utils.ts`
  - `api/src/providers/retailerA.ts`
  - `api/src/providers/retailerB.ts`
  - `api/src/providers/index.ts`
- Endpoints Node actualizados:
  - `GET /v1/stores` usa agregador
  - `GET /v1/search` soporta `lat`, `lon`, `zoneId`, `includeExternalLinks`
  - `POST /v1/list-totals` retorna `totals`, `coverage`, `warnings`
  - `GET /v1/price-history` usa snapshots del agregador
- Contratos OpenAPI y tipos compartidos actualizados con trazabilidad:
  - `Product.externalUrl`, `Product.sourceHints`
  - `PriceSnapshot.source`, `PriceSnapshot.sourceCapturedAt`
  - `ListTotalsResponse.coverage`, `ListTotalsResponse.warnings`
- Web actualizado:
  - Home: busqueda con link-out opcional "Ver en Google Shopping"
  - Compare: cobertura, warnings, fuente, timestamp
  - Tests nuevos para link-out y coverage
- Android preparado sin romper offline:
  - Feature flag persistido (`remote_compare_enabled`) en `UserPreferencesRepository`
  - Compare muestra origen de precios (local/remoto) segun flag

## Decisiones de compliance
- No se ingieren resultados/precios de Google Shopping.
- Google Shopping se usa solo para navegacion externa (link-out).
- Precios internos etiquetados por `source` y timestamp.

## Pendientes para siguiente agente
- Conectar proveedores reales/licenciados en `api/src/providers/*` (reemplazar data mock).
- Definir manejo de credenciales por entorno (variables de entorno y rotacion).
- Extender filtro por ubicacion real (`lat/lon/zoneId`) con logica geografica.
- Agregar tests unitarios de agregacion en `api/` (dedup, coverage, stale warnings, ranking).
- Resolver ejecucion estable de tests en entorno Windows/sandbox (ver bloqueadores abajo).
- Integrar modo remoto real en Android (actualmente solo scaffolding UI/flag).

## Bloqueadores detectados durante validacion
- `packages/contracts`:
  - `node --test` falla en este entorno con `spawn EPERM`.
- `app-web`:
  - `vitest` no disponible localmente (`node_modules/vitest/vitest.mjs` no encontrado).

## Comandos de validacion sugeridos
```bash
# Contratos
cd packages/contracts
npm run generate
npm test

# API Node
cd api
npm run build
npm run dev

# Web
cd app-web
npm install
npm test -- --run
```

## Riesgos y mitigaciones
- Riesgo: una tienda sin cobertura puede distorsionar ranking si no se controla.
  - Mitigacion aplicada: ranking/savings se calcula solo para tiendas con `matchedItems > 0`.
- Riesgo: desincronizacion `openapi.json` vs `generated.ts`.
  - Mitigacion aplicada: flujo de regeneracion + verificacion de sync.
- Riesgo: confusion de origen de datos para usuario final.
  - Mitigacion aplicada: etiqueta visible de fuente y advertencia legal en UI.

## Nota de producto/legal para UI
Usar texto corto consistente:
"Precios pueden variar; verifica en tienda/fuente."
