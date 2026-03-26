import {
    CoverageSummary,
    ListItem,
    PriceSnapshot,
    Product,
    Store,
    StoreTotal
} from "../../../packages/contracts/src/types";

export type { Store, Product, StoreTotal, ListItem, PriceSnapshot };

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "/api";
const API_KEY = import.meta.env.VITE_API_KEY || "dummy-dev-key";

async function fetchJson<T>(path: string, init?: RequestInit): Promise<T> {
    const response = await fetch(`${API_BASE_URL}${path}`, {
        ...init,
        headers: {
            "Content-Type": "application/json",
            "X-API-Key": API_KEY,
            ...(init?.headers || {}),
        },
    });
    if (!response.ok) {
        throw new Error(`Request failed: ${response.status}`);
    }
    return response.json() as Promise<T>;
}

export async function fetchStores(): Promise<Store[]> {
    const data = await fetchJson<{ stores: Store[] }>("/v1/stores");
    return data.stores;
}

export interface SearchProductsOptions {
    includeExternalLinks?: boolean;
}

export async function searchProducts(q: string, options: SearchProductsOptions = {}): Promise<Product[]> {
    const params = new URLSearchParams({ q });
    if (options.includeExternalLinks) {
        params.set("includeExternalLinks", "true");
    }
    const data = await fetchJson<{ products: Product[] }>(
        `/v1/search?${params.toString()}`
    );
    return data.products;
}

export interface ListTotalsApiResponse {
    totals: StoreTotal[];
    coverage: CoverageSummary;
    warnings?: string[] | null;
}

export async function calculateListTotals(items: ListItem[]): Promise<ListTotalsApiResponse> {
    return fetchJson<ListTotalsApiResponse>("/v1/list-totals", {
        method: "POST",
        body: JSON.stringify({ items })
    });
}

export async function fetchPriceHistory(productId: string): Promise<{ product: Product; history: PriceSnapshot[] }> {
    return fetchJson<{ product: Product; history: PriceSnapshot[] }>(
        `/v1/price-history?productId=${encodeURIComponent(productId)}`
    );
}
