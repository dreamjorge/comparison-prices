import { openApiSpec } from "@comparison-prices/contracts";

const API_BASE = "/api";

const HEADERS = {
    "Content-Type": "application/json",
    "X-API-Key": "dummy-dev-key", // Mock for dev environment
};

export type Store = typeof openApiSpec.components.schemas.Store & { id: string };
export type Product = typeof openApiSpec.components.schemas.Product & { id: string };
export type StoreTotal = typeof openApiSpec.components.schemas.StoreTotal & { storeId: string };

export async function fetchStores() {
    const res = await fetch(`${API_BASE}/stores`, { headers: HEADERS });
    if (!res.ok) throw new Error("Failed to fetch stores");
    return res.json() as Promise<{ stores: Store[] }>;
}

export async function searchProducts(query: string) {
    const res = await fetch(`${API_BASE}/search?q=${encodeURIComponent(query)}`, { headers: HEADERS });
    if (!res.ok) throw new Error("Failed to search products");
    return res.json() as Promise<{ products: Product[] }>;
}

export async function fetchListTotals(items: { productId: string; quantity: number }[]) {
    const res = await fetch(`${API_BASE}/list-totals`, {
        method: "POST",
        headers: HEADERS,
        body: JSON.stringify({ items }),
    });
    if (!res.ok) throw new Error("Failed to fetch list totals");
    return res.json() as Promise<{ totals: StoreTotal[] }>;
}
