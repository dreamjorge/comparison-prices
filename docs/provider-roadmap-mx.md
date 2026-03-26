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

## Matriz de decisión
Escala sugerida: 1 (bajo) a 5 (alto)

| Proveedor | Cobertura MX | Frescura | Riesgo legal | Esfuerzo integración | Costo | Comentarios |
| --- | --- | --- | --- | --- | --- | --- |
| PROFECO QQP | 3 | 3 | 1 | 2 | 1 | Mejor entrada legal para baseline de precios; no cubre todo el catálogo ni real-time. |
| Retailer partner feed A | 5 | 4 | 1 | 4 | 4 | Mejor calidad/coverage, requiere convenio comercial y SLA. |
| Mercado Libre MLM | 4 | 4 | 1 | 3 | 2 | Complementa categorías retail/ecommerce; no reemplaza supermercados físicos. |
| Walmart Marketplace MX | 3 | 4 | 1 | 4 | 3 | Solo viable con acceso oficial de partner aprobado. |
| Open Food Facts/Open Prices | 2 | 2 | 1 | 2 | 1 | Útil para metadata y fallback; cobertura de precio limitada en MX. |

## Ranking final
1. PROFECO QQP (fase inicial obligatoria)
2. Retailer partner feed A (primer source de producción)
3. Mercado Libre MLM (complementario por categoría)
4. Walmart Marketplace MX (si hay acceso partner)
5. Open Food Facts/Open Prices (metadata + fallback)

Rationale:
- PROFECO permite arrancar rápido y legal con cobertura mínima viable.
- El primer partner feed sube precisión/frescura y reduce `unmatchedItems`.
- Mercado Libre aporta amplitud de oferta sin depender de scraping.
- Walmart Marketplace es opcional por requisito de acceso.
- Fuentes open-data quedan como soporte, no como core pricing.

## Diseño de integración (por proveedor)
### 1) PROFECO QQP
- `Provider ID`: `profeco_qqp`
- Auth: ninguna (dataset público)
- Cache default: 24h por corte de dataset
- Mapping mínimo:
  - Product: `name`, `brand` (si disponible), `sizeLabel` (si disponible), `sourceHints=["profeco_qqp"]`
  - PriceSnapshot: `price`, `source="profeco_qqp"`, `sourceCapturedAt`
- Fallback:
  - Si no hay coincidencia por zona, responder con warning de cobertura parcial.

### 2) Retailer partner feed A
- `Provider ID`: `retailer_partner_a`
- Auth: API key u OAuth según contrato
- Cache default: 15–60 min según rate-limit contractual
- Mapping mínimo:
  - Product completo con SKU externo determinístico
  - PriceSnapshot con `sourceCapturedAt` de feed o timestamp de ingestión
- Fallback:
  - Si API falla, mantener último snapshot válido y warning `stale`.

### 3) Mercado Libre MLM
- `Provider ID`: `meli_mlm`
- Auth: credenciales de app en plataforma developers
- Cache default: 15 min por query/zone/category
- Mapping mínimo:
  - Product: título + atributos normalizados a `brand/sizeLabel`
  - PriceSnapshot: precio publicado + `source="meli_mlm"`
- Fallback:
  - Excluir de cálculo principal si no cumple normalización de unidad/tamaño.

### 4) Walmart Marketplace MX (condicional)
- `Provider ID`: `walmart_mx_marketplace`
- Habilitar solo con acceso legal confirmado.

### Reglas comunes
- Normalizar marca/texto/unidad en capa compartida (`api/src/providers/utils.ts`).
- Aplicar dedup por `normalizedKey`.
- Exponer `coverage` y `warnings` en `/list-totals`.

## Plan de implementación 30/60/90 días
### Día 0–30
- Implementar `profeco_qqp` provider y pipeline de ingestión batch.
- Publicar dashboard base de métricas (`coverage`, `unmatchedItems`, `staleness_hours`).
- Definir política de cache/rate y kill switch por provider.

### Día 31–60
- Integrar `retailer_partner_a` con credenciales por entorno.
- Activar ranking multi-source con prioridad por frescura y confianza.
- Afinar matching de unidad/tamaño y tolerancias de equivalencia.

### Día 61–90
- Integrar `meli_mlm` como complemento en categorías seleccionadas.
- Ajustar score de confianza por source y calidad de match.
- Evaluar segundo partner feed retailer en zonas de baja cobertura.

## Backlog derivado inmediato
- [ ] Crear `api/src/providers/profecoQqp.ts`.
- [ ] Definir contrato de configuración por provider (`env` + secretos).
- [ ] Añadir pruebas de integración para dedup/ranking/coverage.
- [ ] Añadir tablero básico de observabilidad por provider.

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
