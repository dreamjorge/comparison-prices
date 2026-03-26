# app-web

## Environment
Copy `.env.example` to `.env` when you need custom API values.

- `VITE_API_BASE_URL`: base URL used by `src/api/client.ts` (default `/api`)
- `VITE_API_KEY`: API key sent as `X-API-Key` (default `dummy-dev-key`)

## Local development
```bash
npm install
npm run dev
```

By default, Vite proxies `/api` to `http://localhost:4000`.
