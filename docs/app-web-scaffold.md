# Scaffold de frontend web (TICKET 9.2)

## Alcance inicial
- React + Vite + TypeScript.
- Routing con `react-router-dom` y layout base compartido.
- Vistas demo: resumen, comparador y 404.
- Estilos básicos en `styles.css`.

## Desarrollo local
```bash
cd app-web
npm install
npm run dev
```

## Variables de entorno web
- `VITE_API_BASE_URL`: base de API para el cliente (`/api` por defecto para usar el proxy de Vite).
- `VITE_API_KEY`: API key enviada como `X-API-Key` (`dummy-dev-key` por defecto en desarrollo).
- Plantilla disponible en `app-web/.env.example`.

## Issues potenciales y mitigaciones
- **Issue:** Los datos demo podrían interpretarse como reales en entornos de pruebas.
  **Mitigación:** etiquetar explícitamente los bloques demo y reemplazarlos al integrar los contratos del backend.
- **Issue:** La navegación podría perder estado al cambiar a una estrategia de routing con base path.
  **Mitigación:** centralizar el base path en el router antes de integrar despliegues en subcarpetas.
