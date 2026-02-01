# Propuesta de estructura monorepo (web + backend compartido)

## Objetivo
Unificar la web y el backend/API en el mismo repositorio para compartir contratos y acelerar el delivery, sin bloquear el trabajo móvil existente.

## Estructura propuesta
```
/
├─ app-android/           # App Android (existente)
├─ app-web/               # Frontend web (nuevo)
├─ api/                   # Backend/API (nuevo)
├─ packages/
│  └─ contracts/          # OpenAPI/DTOs compartidos + generación de tipos
├─ docs/
└─ tasks.md
```

## Cómo encaja con el backend/API
- **`packages/contracts` como fuente de verdad**: OpenAPI/JSON Schema con DTOs para listados, productos, precios y listas.
- **Generación de tipos**:
  - Web: tipos TypeScript y cliente HTTP (fetch/axios) basados en OpenAPI.
  - Android: cliente Kotlin opcional o mapeo manual usando el mismo esquema para evitar divergencias.
- **API alineada con la web**: endpoints mínimos que soportan el flujo de comparación (tiendas, búsqueda, totales de lista, historial de precios).

## Convenciones sugeridas
- **Versionado de API**: prefijo `/v1`.
- **Paginado**: `limit` + `cursor` (o `page`) consistente en todos los listados.
- **Autenticación**:
  - Público: catálogo de tiendas y búsqueda básica.
  - Protegido: listas del usuario, historial personalizado y snapshots privados.
- **CORS**: permitir origen del frontend web (y staging) con allowlist explícita.

## Notas de compatibilidad
- La app Android puede seguir usando datos locales mientras se integra gradualmente la API.
- El backend debe exponer datos mínimos para web sin romper los flows actuales.
