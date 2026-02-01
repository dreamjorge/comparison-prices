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

## Issues potenciales y mitigaciones
- **Issue:** Los datos demo podrían interpretarse como reales en entornos de pruebas.
  **Mitigación:** etiquetar explícitamente los bloques demo y reemplazarlos al integrar los contratos del backend.
- **Issue:** La navegación podría perder estado al cambiar a una estrategia de routing con base path.
  **Mitigación:** centralizar el base path en el router antes de integrar despliegues en subcarpetas.
