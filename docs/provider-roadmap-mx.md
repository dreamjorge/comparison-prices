# Provider Roadmap MX (TICKET 11.5)

## Objetivo
Definir e implementar un roadmap de APIs/fuentes legales para México, maximizando cobertura de precios y minimizando riesgo legal/compliance.

## Contexto
- Base técnica existente:
  - Agregador compliant implementado en `api/src/providers/`.
  - Contrato con `source`, `coverage`, `warnings`, `externalUrl`.
  - Link-out externo habilitado sin ingesta de Google Shopping.
- Documento de handoff técnico:
  - `docs/handoff-ticket-11.3.md`

## Supuestos no negociables (compliance)
- No scraping no autorizado de Google Shopping/Search.
- No reutilización de catálogos protegidos sin permiso/licencia.
- Siempre etiquetar fuente y timestamp de actualización en UI.
- Mantener texto legal: `Precios pueden variar; verifica en tienda/fuente.`

## Candidatos de proveedores (MX)
1. PROFECO QQP (datos abiertos)
2. APIs/feeds de retailers con acuerdo comercial
3. Mercado Libre API (MLM) como complemento
4. Walmart Marketplace MX (solo partner aprobado)
5. Fuentes open-data/crowd como complemento de metadata

## Matriz de decisión (llenar)
Escala sugerida: 1 (bajo) a 5 (alto)

| Proveedor | Cobertura MX | Frescura | Riesgo legal | Esfuerzo integración | Costo | Comentarios |
| --- | --- | --- | --- | --- | --- | --- |
| PROFECO QQP |  |  |  |  |  |  |
| Retailer partner feed A |  |  |  |  |  |  |
| Mercado Libre MLM |  |  |  |  |  |  |
| Walmart Marketplace MX |  |  |  |  |  |  |
| Open Food Facts/Open Prices |  |  |  |  |  |  |

## Ranking final (llenar)
1. 
2. 
3. 
4. 
5. 

Rationale:
- 

## Diseño de integración (por proveedor)
Para cada proveedor seleccionado, documentar:
- `Provider ID` (ej. `profeco_qqp`, `retailer_partner_a`)
- Endpoint/base URL
- Auth requerida (API key/OAuth/ninguna)
- Límite de rate y estrategia de cache
- Campos mínimos mapeados a contrato:
  - Product: `id`, `name`, `brand`, `sizeLabel`, `sourceHints`
  - PriceSnapshot: `productId`, `storeId`, `price`, `source`, `sourceCapturedAt`
- Estrategia de normalización:
  - unidades (ml/l, g/kg), marca, tamaño, claves determinísticas
- Reglas de fallback:
  - sin cobertura, stale data, error de provider

## Plan de implementación 30/60/90 días
### Día 0–30
- Integrar PROFECO QQP como proveedor inicial.
- Medir `coverage` y `unmatchedItems` por zona.
- Definir alertas de calidad de datos (staleness).

### Día 31–60
- Integrar primer retailer partner feed licenciado.
- Ajustar ranking multi-provider por frescura y calidad.
- Mejorar matching por unidad/tamaño.

### Día 61–90
- Integrar Mercado Libre como fuente complementaria por categoría.
- Optimizar score de confianza por source.
- Evaluar expansión a 2do retailer partner.

## Métricas de éxito
- `%coverage` global y por tienda/zona
- `%unmatchedItems` por lista
- `staleness_hours` P50/P95 por source
- `source_mix` (porcentaje de items por proveedor)
- Latencia de `/search` y `/list-totals` (P50/P95)

## Checklist legal/compliance
- [ ] ToS revisados y documentados por proveedor.
- [ ] Atribución requerida implementada en UI.
- [ ] Restricciones de almacenamiento y redistribución documentadas.
- [ ] Rate limits respetados con retry/backoff/cache.
- [ ] Política interna de desactivación rápida de provider (kill switch).

## QA y pruebas
- [ ] Pruebas unitarias de mapeo por proveedor.
- [ ] Pruebas de dedup/ranking multi-provider.
- [ ] Pruebas de cobertura parcial y warnings.
- [ ] Pruebas de fallback ante error/timeouts.

## Riesgos y mitigaciones
- Riesgo: baja cobertura en algunas zonas.
  - Mitigación: blend de proveedores + visibilidad de `coverage`.
- Riesgo: cambios en términos de proveedor.
  - Mitigación: revisión periódica legal + kill switch por source.
- Riesgo: datos stale en comparador.
  - Mitigación: umbral de staleness + warnings + refresco programado.

## Owners propuestos
- Agent 3 (Data): evaluación de proveedores, mapeo, ingestión.
- Agent Web/API: integración de adapters y observabilidad.
- Agent 6 (QA/Compliance): validación legal, checklist de release.

## Referencias
- PROFECO datos abiertos: https://datos.profeco.gob.mx/datos_abiertos/
- Mercado Libre Developers MX: https://developers.mercadolibre.com.mx/
- Walmart Marketplace MX: https://developer.walmart.com/mx/
