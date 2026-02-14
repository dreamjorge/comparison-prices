# Data Schema & Normalization Rules

This document outlines how product data is structured and normalized in the Comparison Prices API.

## Product Entity
All products must follow the `Product` schema in `openapi.json`.

| Field | Description | Normalization Rule |
| :--- | :--- | :--- |
| `id` | Unique identifier | Immutable UUID or Slug |
| `name` | Descriptive name | Content-indexed, lowercase for search |
| `brand` | Manufacturer/Brand | Special chars removed, trimmed |
| `sizeLabel`| Size/Quantity label | Normalized to base units (L, kg, pz) |
| `externalUrl` | External validation URL | Optional; only for link-out navigation |
| `sourceHints` | Data provenance hints | Provider IDs used in aggregation |

## Normalization Logic (`NormalizationService`)

### Brand Cleaning
- Convert to lowercase.
- Remove all non-alphanumeric characters.
- Trim whitespace.

### Unit Conversion
The API attempts to parse `sizeLabel` to create a numeric value for comparison:

| Input Pattern | Standard Unit | Multiplier |
| :--- | :--- | :--- |
| `(\d+)ml`, `(\d+) mililitros` | `L` | `value / 1000` |
| `(\d+)l`, `(\d+) litro` | `L` | `value` |
| `(\d+)g`, `(\d+) gramos` | `kg` | `value / 1000` |
| `(\d+)kg`, `(\d+) kilos` | `kg` | `value` |
| `(\d+)pz`, `(\d+) piezas` | `pz` | `value` |

## Search Logic
Search queries are automatically matched against a generated "search key":
`{clean_brand} {lowercase_name}`

## Source Traceability
- `PriceSnapshot.source` indicates the internal provider feed used for the snapshot.
- `PriceSnapshot.sourceCapturedAt` tracks provider freshness.
- `/list-totals` returns `coverage` and optional `warnings` for missing or stale data.
- Google Shopping is used only as external navigation (`externalUrl`), not as ingested price data.
