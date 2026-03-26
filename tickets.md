# 📋 Backlog de Tickets — Android App Comparador de Precios (LATAM)

## Status summary (2026-01-25)
- TICKET 0.1: Drafted in `docs/mvp-scope.md`
- TICKET 0.2: Drafted in `docs/wireframes/ux-flow.md` + `docs/wireframes/notes.md`
- TICKET 1.1: Scaffold created in `app-android/` (needs build validation)
- TICKET 1.2, 1.3, 2.2, 2.4, 8.3, 8.6: Implemented en `app-android/` con tests y migración de datos.
- TICKET 1.2+: Pending

## Epic 0 — Preparación del proyecto
---

### TICKET 0.1 — Definir alcance del MVP
**Tipo:** Task  
**Prioridad:** P0  
**Owner:** Agent 1 (Product)

**Descripción**
Definir exactamente qué entra y qué NO entra en el MVP para evitar scope creep.

**Criterios de aceptación**
- Documento con features P0 / P1 / P2
- Definición clara de público objetivo (LATAM)
- Definición de modelo Free vs Pro

---

### TICKET 0.2 — Wireframes y flujo UX
**Tipo:** Design  
**Prioridad:** P0  
**Owner:** Agent 1 (UX)

**Descripción**
Diseñar wireframes de las pantallas clave.

**Pantallas**
- Home (lista de compras)
- Comparador por tienda
- Detalle de producto
- Configuración (zona / tiendas)
- Paywall

**Criterios de aceptación**
- Wireframes en Figma o PDF
- Flujo completo documentado
- Ubicación clara de ads permitidos

---

## Epic 1 — Base Android
---

### TICKET 1.1 — Crear proyecto Android base
**Tipo:** Task  
**Prioridad:** P0  
**Owner:** Agent 2 (Android)

**Descripción**
Inicializar proyecto Android moderno.

**Requisitos**
- Kotlin
- Jetpack Compose
- Room
- Hilt
- Material 3

**Criterios de aceptación**
- Proyecto compila
- Navegación básica funcionando
- Arquitectura limpia (UI / Domain / Data)

---

### TICKET 1.2 — Modelos de datos locales
**Tipo:** Task  
**Prioridad:** P0  
**Owner:** Agent 2
**Status:** Done (2026-02-01)

**Descripción**
Definir entidades de base de datos.

**Entidades**
- Product
- Store
- PriceSnapshot
- ShoppingList
- ListItem

**Criterios de aceptación**
- Room entities creadas
- DAOs funcionales
- Migraciones definidas

---

### TICKET 1.3 — Seeding demo data idempotente en ViewModels
**Tipo:** Task  
**Prioridad:** P0  
**Owner:** Agent 2
**Status:** Done (2026-02-01)

**Descripción**
across ViewModels

If Home and Compare ViewModels are created close together (e.g., user switches tabs right after launch or Compose preloads destinations), both will call seedDemoDataIfNeeded concurrently. Because that helper only checks shoppingListDao.count() before inserting and isn’t atomic, both coroutines can observe count=0 and insert duplicate demo lists/items. That leaves multiple “Compra semanal” lists and makes observeLatestList() pick whichever insert finishes last. Consider centralizing seeding in a single owner or making the seed operation transactional/unique so it’s truly idempotente.

---

## Epic 2 — Lógica de negocio
---

### TICKET 2.1 — Crear y editar lista de compras
**Tipo:** Feature  
**Prioridad:** P0  
**Owner:** Agent 2

**Descripción**
El usuario puede crear y editar una lista de compras.

**Criterios de aceptación**
- Agregar / eliminar productos
- Cambiar cantidad
- Persistencia local
- UX fluido

---

### TICKET 2.2 — Comparador de precios por tienda
**Tipo:** Feature  
**Prioridad:** P0  
**Owner:** Agent 2
**Status:** Done (2026-02-01)

**Descripción**
Calcular el total de la lista por tienda.

**Criterios de aceptación**
- Mostrar total por tienda
- Ordenar de más barato a más caro
- Mostrar ahorro vs segunda opción

---

### TICKET 2.3 — Historial de precios
**Tipo:** Feature  
**Prioridad:** P0  
**Owner:** Agent 2

**Descripción**
Mostrar evolución del precio de un producto.

**Criterios de aceptación**
- Historial mínimo 7 días
- Gráfica simple
- Funciona offline

---

### TICKET 2.4 — Encabezado dinámico y ahorro por tienda en comparador
**Tipo:** Feature  
**Prioridad:** P1  
**Owner:** Agent 2
**Status:** Done (2026-02-01)

**Descripción**
Usar datos reales de la lista para mostrar el nombre de la lista y la fecha actual, y calcular el ahorro contra la siguiente tienda más barata en el comparador.

**Criterios de aceptación**
- El encabezado del comparador usa el nombre real de la lista activa.
- La fecha usa el locale del usuario (no hardcode).
- El ahorro vs siguiente tienda se calcula con datos reales (no mock).
- Tests unitarios para el cálculo de ahorro y el formateo de moneda/fecha.

---

## Epic 3 — Datos y normalización
---

### TICKET 3.1 — Dataset mock LATAM
**Tipo:** Task  
**Prioridad:** P0  
**Owner:** Agent 3 (Data)

**Descripción**
Crear dataset de prueba realista.

**Requisitos**
- 200+ productos
- 3–4 supermercados
- Variaciones de precios

**Criterios de aceptación**
- Dataset usable en app
- Datos coherentes (unidades, marcas)

---

### TICKET 3.2 — Normalización de productos
**Tipo:** Feature  
**Prioridad:** P1  
**Owner:** Agent 3

**Descripción**
Unificar productos similares entre tiendas.

**Criterios de aceptación**
- Normalización por tamaño/unidad
- Matching básico por nombre
- Documentación del algoritmo

---

### TICKET 3.3 — Fetch de precios reales (proveedores web/API)
**Tipo:** Feature  
**Prioridad:** P1  
**Owner:** Agent 3 (Data) + Agent 2 (Android)

**Descripción**
Integrar una fuente real de precios (API o feed permitido) para reemplazar datos mock en el comparador.

**Criterios de aceptación**
- Conector a una fuente con permisos explícitos (API/feeds)
- Endpoint o repositorio de datos consumible por la app
- Sin scraping no autorizado
- Documentación de límites de uso y actualización

---

### TICKET 3.4 — Cambios de precios por ubicación cercana
**Tipo:** Feature  
**Prioridad:** P1  
**Owner:** Agent 3 (Data) + Agent 2 (Android)

**Descripción**
Agregar soporte para cambios de precios por ubicación, garantizando que cuando se consulten precios exista cobertura en una ubicación cercana.

**Criterios de aceptación**
- Los precios se asocian a una ubicación (zona/sucursal) con coordenadas o identificador.
- Al consultar precios, se requiere una ubicación de referencia y se seleccionan precios de la ubicación más cercana disponible.
- Se define y documenta un radio máximo de cercanía (fallback si no hay precios dentro del radio).
- Pruebas que validen la selección por cercanía y el fallback cuando no hay cobertura.

---

## Epic 4 — Matching avanzado (opcional Rust)
---

### TICKET 4.1 — Fuzzy matching de productos
**Tipo:** Feature  
**Prioridad:** P1  
**Owner:** Agent 4 (Rust)

**Descripción**
Implementar fuzzy matching para productos equivalentes.

**Criterios de aceptación**
- Devuelve ranking de candidatos
- Score visible
- Casos reales LATAM probados

---

## Epic 5 — Alertas y automatización
---

### TICKET 5.1 — Alertas locales de precios
**Tipo:** Feature  
**Prioridad:** P0  
**Owner:** Agent 2

**Descripción**
Notificar cuando un producto baja de precio.

**Criterios de aceptación**
- Usa WorkManager
- Alertas configurables
- No requiere backend

---

### TICKET 5.2 — Alertas de “lista más barata”
**Tipo:** Feature  
**Prioridad:** P1  
**Owner:** Agent 2

**Descripción**
Avisar si otra tienda se vuelve la mejor opción.

**Criterios de aceptación**
- Comparación automática
- Notificación clara
- Respeta frecuencia

---

## Epic 6 — Monetización
---

### TICKET 6.1 — Integración AdMob banner
**Tipo:** Feature  
**Prioridad:** P0  
**Owner:** Agent 5

**Descripción**
Agregar banner pequeño en versión Free.

**Criterios de aceptación**
- Solo pantallas pasivas
- No interrumpe acciones
- Cumple políticas Play Store

---

### TICKET 6.2 — Rewarded ads
**Tipo:** Feature  
**Prioridad:** P1  
**Owner:** Agent 5

**Descripción**
Desbloquear features temporales con ads.

**Ejemplos**
- Historial completo 24h
- Comparar más tiendas

**Criterios de aceptación**
- Usuario inicia el ad
- Desbloqueo temporal correcto

---

### TICKET 6.3 — Paywall Pro (sin ads)
**Tipo:** Feature  
**Prioridad:** P0  
**Owner:** Agent 5

**Descripción**
Implementar versión Pro.

**Criterios de aceptación**
- Compra funcional
- Ads desactivados
- Features Pro habilitados

---

## Epic 7 — Calidad y Release
---

### TICKET 7.1 — QA funcional
**Tipo:** Task  
**Prioridad:** P0  
**Owner:** Agent 6 (QA)

**Descripción**
Pruebas funcionales completas.

**Criterios de aceptación**
- No crashes
- Flujos principales OK
- Performance aceptable

---

### TICKET 7.2 — Checklist Play Store
**Tipo:** Task  
**Prioridad:** P0  
**Owner:** Agent 6

**Descripción**
Preparar todo para publicación.

**Incluye**
- Data Safety
- Política de ads
- Screenshots
- Descripción

---

### TICKET 7.3 — Build AAB + beta
**Tipo:** Task  
**Prioridad:** P0  
**Owner:** Agent 6

**Descripción**
Generar AAB y subir a beta interna.

**Criterios de aceptación**
- AAB válido
- Beta activa
- Crash reporting habilitado

---

## 🎯 Definición de “Done” del MVP
- Usuario puede comparar su lista en varias tiendas
- Alertas funcionando
- Monetización activa
- App estable en beta
- Lista para Play Store

---

### TICKET 8.1 — Auditoría de seeding y coverage de tests
**Tipo:** Task  
**Prioridad:** P1  
**Owner:** Agent 2

**Descripción**
Revisar la estrategia de seeding de datos demo y ampliar los unit tests de concurrencia/atomicidad para evitar duplicados en escenarios de navegación rápida.

**Criterios de aceptación**
- Tests que simulen llamadas concurrentes a seeding sin duplicados.
- Documentación de riesgos conocidos y mitigaciones.

---

### TICKET 8.2 — Endurecer deduplicación de productos demo
**Tipo:** Task  
**Prioridad:** P2  
**Owner:** Agent 2

**Descripción**
Definir una estrategia de deduplicación para productos demo (por ejemplo, índices únicos por nombre+marca o IDs determinísticos) para evitar duplicados si se agregan nuevos flows de seeding.

**Criterios de aceptación**
- Propuesta documentada en el código o docs.
- Se valida que los productos demo no se dupliquen en escenarios de re-seed.

---

### TICKET 8.3 — Ajustar totales del comparador según cantidades de la lista
**Tipo:** Feature  
**Prioridad:** P2  
**Owner:** Agent 2
**Status:** Done (2026-02-01)

**Descripción**
Actualizar el comparador para que los totales por tienda respeten las cantidades de cada ítem en la lista (multiplicando el precio unitario por la cantidad y unidad).

**Criterios de aceptación**
- Los totales por tienda reflejan cantidades reales de la lista.
- Tests unitarios que validan el cálculo con cantidades distintas a 1.

---

### TICKET 8.4 — Vincular cantidades del comparador a IDs de producto
**Tipo:** Feature  
**Prioridad:** P2  
**Owner:** Agent 2

**Descripción**
Evitar depender del nombre del producto para aplicar cantidades al total por tienda.

**Criterios de aceptación**
- Los totales usan el ID del producto (o una clave determinística) para aplicar cantidades.
- Se documenta cómo se mapea el catálogo de precios a productos de la lista.

---

### TICKET 8.5 — Documentar deduplicación de datos demo
**Tipo:** Task  
**Prioridad:** P2  
**Owner:** Agent 2

**Descripción**
Documentar una estrategia de deduplicación para datos demo (productos/listas) que evite duplicados cuando el seeding se dispare en paralelo.

**Criterios de aceptación**
- Documento con propuesta de clave natural/índices únicos para productos demo.
- Recomendaciones para seeding idempotente y tests asociados.

---

### TICKET 8.6 — Normalizar brand nulo en productos demo
**Tipo:** Task  
**Prioridad:** P2  
**Owner:** Agent 2
**Status:** Done (2026-02-01)

**Descripción**
Evitar duplicados permitidos por `NULL` en índices únicos al normalizar `brand` para productos demo.

**Criterios de aceptación**
- Definir regla de normalización (`brand` vacío en lugar de nulo) antes de insertar productos demo.
- Actualizar seeding y/o migraciones para alinear registros existentes.

**Riesgos y mitigaciones**
- Riesgo: inconsistencias si alguna capa espera `NULL` como ausencia de marca.
- Mitigación: normalizar `brand` en seeding/migraciones y tratar `\"\"` como “sin marca” en UI/consultas.

### TICKET 8.7 — Normalizar brand vacío en UI y repositorios
**Tipo:** Task  
**Prioridad:** P2  
**Owner:** Agent 2

**Descripción**
Alinear la capa de presentación y repositorios para que `""` se interprete como “sin marca” y evitar divergencias con datos legacy.

**Criterios de aceptación**
- Helper compartido para mapear `""` a valor legible en UI.
- Pruebas unitarias que validen el mapeo en casos nulos/vacíos.

---

### TICKET 8.8 — Revisar merge de cantidades en deduplicación de list_items
**Tipo:** Task  
**Prioridad:** P2  
**Owner:** Agent 2

**Descripción**
Cuando una migración remapea `productId` y deduplica `list_items`, es posible que existan items con cantidades distintas para el mismo `listId` y `productId`. Validar si corresponde fusionar cantidades en lugar de conservar solo el primer registro.

**Criterios de aceptación**
- Análisis del impacto de la deduplicación actual (por `MIN(id)`).
- Definición de estrategia para fusionar cantidades o mantener el comportamiento actual.
- Actualizar migraciones/tests si se decide fusionar.

**Análisis y decisión (2026-02-08):**
La estrategia actual (`MIN(id)` = "first-item wins") es correcta para el MVP:
- En el contexto de seeding demo data, los items duplicados son artefactos de la migración, no data real del usuario.
- Fusionar cantidades (`SUM(quantity)`) en una migración destructiva podría doblar cantidades si el usuario ya tenía datos correctos.
- Decisión: **mantener `MIN(id)` como comportamiento predeterminado** para migraciones. Si en Phase 2 se detectan casos reales de fusión necesaria, se agregará una opción de merge explícita en la UI.

**Status:** Done

---

# 🚀 Phase 2: Scalability & Real Utility

## Epic 11 — Real Data Pipeline
---
### TICKET 11.1 — Ingestor de Precios Real-time
**Tipo:** Feature | **Prioridad:** P0
**Descripción**: Crear un servicio de scraping/ingesta que alimente el backend con precios reales.
**Tareas**:
- [ ] Implementar scaffold con Playwright/Python.
- [ ] Mapear selectores para 2 supermercados principales.
- [ ] Guardar resultados en JSON/DB.

### TICKET 11.2 — Migración a Base de Datos de Producción
**Tipo:** Task | **Prioridad:** P0
**Descripción**: Reemplazar `MOCK_PRODUCTS` en `main.py` por una conexión a PostgreSQL.
**Criterios de Aceptación**:
- [ ] Docker Compose con PostgreSQL + FastAPI.
- [ ] Scripts de migración inicial.

## Epic 12 — Cloud Sync & Auth
---
### TICKET 12.1 — Implementación de Firebase Auth
**Tipo:** Task | **Prioridad:** P1
**Descripción**: Permitir que los usuarios inicien sesión en Android y Web.
**Criterios de Aceptación**:
- [ ] Login con Google habilitado en Android.
- [ ] Token de auth validado en el backend.

### TICKET 12.2 — Sincronización de Listas
**Tipo:** Feature | **Prioridad:** P1
**Descripción**: Sincronizar `ShoppingList` local (Room) con la nube.
**Tareas**:
- [ ] Worker de sincronización periódica.
- [ ] Manejo de conflictos básico (LWW - Last Writer Wins).

## Epic 13 — Inteligencia de Precios
---
### TICKET 13.1 — Algoritmo "Mejor Día para Comprar"
**Tipo:** Feature | **Prioridad:** P2
**Descripción**: Analizar el historial de precios para predecir fluctuaciones.
**Criterios de Aceptación**:
- [ ] Gráfica de tendencia en Android.
- [ ] Recomendación textual (ej: "Suele bajar los martes").

### TICKET 13.2 — Evaluar librería de gráficos para historial de precios
**Tipo:** Task | **Prioridad:** P2
**Descripción**: Definir si se mantiene un gráfico nativo simple o se integra una librería externa con soporte de ejes y tooltips.
**Criterios de Aceptación**:
- [ ] Documentar pros/contras y tamaño de APK.
- [ ] Probar al menos una librería con el historial de precios real.

---

### TICKET 11.3 — Integración compliant de fuentes externas + link-out Google Shopping
**Tipo:** Feature  
**Prioridad:** P1  
**Owner:** Agent 3 + Agent 2 + Agent Web/API
**Status:** In Progress (2026-02-14)

**Descripción**
Integrar agregación de proveedores permitidos en backend y exponer links de salida a Google Shopping sin ingerir resultados de Google.

**Criterios de aceptación**
- [x] Agregador de proveedores en `api/` sin scraping no autorizado.
- [x] Contrato API con `source`, `coverage`, `warnings`.
- [x] UI web/android con link-out opcional y etiquetado de fuente.
- [ ] Pruebas de contrato, agregación y fallback local.

**Notas de implementación**
- Ver handoff técnico para siguiente agente: `docs/handoff-ticket-11.3.md`.

---

### TICKET 11.4 — Hardening de pruebas y rollout para integración compliant
**Tipo:** Task  
**Prioridad:** P1  
**Owner:** Agent 6 (QA) + Agent Web/API

**Descripción**
Endurecer pruebas automáticas y validación cross-platform para la integración de proveedores y link-out compliant.

**Criterios de aceptación**
- [ ] Suite de tests API para dedup/ranking/coverage/warnings.
- [ ] Ejecución confiable de tests de contratos en Windows y CI.
- [ ] Ejecución de tests web en entorno limpio con dependencias instaladas.
- [ ] Checklist de rollout con validación de textos legales y etiquetado de fuente.

---

### TICKET 11.5 — Selección e integración de APIs legales para México (Roadmap proveedores)
**Tipo:** Task  
**Prioridad:** P1  
**Owner:** Agent 3 (Data) + Agent Web/API + Agent 6 (QA/Compliance)

**Descripción**
Definir e implementar roadmap de fuentes legales para México, priorizando cobertura real y bajo riesgo legal para precios de supermercado/ecommerce.

**Alcance**
- Priorizar y justificar proveedores para MX:
  - PROFECO QQP (datos abiertos)
  - APIs/feeds de retailers con convenio
  - Mercado Libre (MLM) como fuente complementaria
- Formalizar contratos de cada proveedor en `api/src/providers/`.
- Definir reglas de etiquetado de fuente, frescura y cobertura para UI.

**Fuera de alcance**
- Scraping no autorizado de Google Shopping/Search.
- Ingesta de catálogos protegidos sin permiso de uso.

**Entregables**
- Documento de decisión técnica y legal en `docs/provider-roadmap-mx.md`.
- Matriz comparativa por proveedor (cobertura, frescura, costo, riesgo legal, esfuerzo).
- Plan por fases (30/60/90 días) y orden de implementación.
- Definición de métricas: `%coverage`, `%unmatchedItems`, `staleness_hours`, `source_mix`.

**Criterios de aceptación**
- [ ] Existe ranking final de proveedores para MX con rationale.
- [ ] Existe definición de implementación por fases con owners.
- [ ] Existe checklist compliance (atribución, términos de uso, límites de rate).
- [ ] Se actualiza backlog con tareas técnicas derivadas (adaptadores concretos por proveedor).

**Dependencias**
- TICKET 11.3 (base de agregador y contrato compliant).
- TICKET 11.4 (hardening de pruebas y rollout).

**Notas**
- Usar texto legal estandarizado en UI: "Precios pueden variar; verifica en tienda/fuente."
- Mantener `externalUrl` únicamente como navegación externa opcional.
- Documento base para ejecución: `docs/provider-roadmap-mx.md`.
