# Estrategia de deduplicación para datos demo

## Objetivo
Evitar duplicados en datos demo aunque el seeding se dispare múltiples veces (por navegación rápida o reintentos). El foco es proteger la tabla de productos y la lista demo para que no se generen entradas repetidas.

## Riesgos actuales
- El seeding concurrente puede intentar insertar productos idénticos más de una vez.
- Los productos demo no tienen una clave natural definida en la base, por lo que Room no evita duplicados automáticamente.

## Propuesta (MVP)
1) **Clave natural para productos demo**
   - Definir un identificador lógico usando `name + brand + defaultUnit` en minúsculas.
   - Usar esa clave para buscar productos existentes antes de insertar nuevos.

2) **Índice único en productos**
   - Agregar un índice único sobre `(name, brand, defaultUnit)`.
   - Usar `@Index(value = ["name", "brand", "defaultUnit"], unique = true)`.
   - En el seeding, usar `insertIgnore` (o `upsert`) para evitar duplicados.

3) **Sembrado idempotente**
   - Mantener el seeding encapsulado en una transacción y protegido por mutex.
   - Validar que `shopping_lists` y `list_items` respeten índices únicos existentes.

## Validación sugerida
- Test unitario que ejecute el seeding simultáneo (2+ corrutinas) y verifique:
  - No hay productos duplicados.
  - La lista demo existe solo una vez.
  - `list_items` mantiene 1 fila por producto (según índice único).

## Riesgos y mitigaciones
- **Brand nulo en productos:** en SQLite los `NULL` permiten duplicados aun con índice único. Mitigación: usar `brand` no nulo en demo data o normalizar `brand` a `""` antes de insertar.
- **IDs faltantes por inserción ignorada:** si `insertIgnore` devuelve `-1`, es necesario resolver el ID existente con la clave natural. Mitigación: consulta por `name+brand+defaultUnit` antes de crear los `list_items`.

## Notas
- Si se agregan nuevos productos demo, deben respetar el esquema de clave natural.
- En el futuro, considerar un `demo_product_key` explícito para desacoplar el matching de nombres.
