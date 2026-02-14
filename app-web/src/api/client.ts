import {
    CoverageSummary,
    ListItem,
    PriceSnapshot,
    Product,
    Store,
    StoreTotal
} from "../../../packages/contracts/src/types";

export type { Store, Product, StoreTotal, ListItem, PriceSnapshot };

const API_BASE_URL = "http://localhost:4000/v1";

export async function fetchStores(): Promise<Store[]> {
    const response = await fetch(`${API_BASE_URL}/stores`);
    if (!response.ok) throw new Error("Failed to fetch stores");
    const data = await response.json();
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
    const response = await fetch(`${API_BASE_URL}/search?${params.toString()}`);
    if (!response.ok) throw new Error("Failed to search products");
    const data = await response.json();
    return data.products;
}

export interface ListTotalsApiResponse {
    totals: StoreTotal[];
    coverage: CoverageSummary;
    warnings?: string[] | null;
}

export async function calculateListTotals(items: ListItem[]): Promise<ListTotalsApiResponse> {
    const response = await fetch(`${API_BASE_URL}/list-totals`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ items })
    });
    if (!response.ok) throw new Error("Failed to calculate totals");
    return response.json() as Promise<ListTotalsApiResponse>;
}

export async function fetchPriceHistory(productId: string): Promise<{ product: Product; history: PriceSnapshot[] }> {
    const response = await fetch(`${API_BASE_URL}/price-history?productId=${productId}`);
    if (!response.ok) throw new Error("Failed to fetch price history");
    return response.json();
}
