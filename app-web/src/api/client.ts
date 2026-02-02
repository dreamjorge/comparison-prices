import { Store, Product, StoreTotal, ListItem } from "../../../packages/contracts/src/types";

export type { Store, Product, StoreTotal, ListItem };

const API_BASE_URL = "http://localhost:4000/v1";

export async function fetchStores(): Promise<Store[]> {
    const response = await fetch(`${API_BASE_URL}/stores`);
    if (!response.ok) throw new Error("Failed to fetch stores");
    const data = await response.json();
    return data.stores;
}

export async function searchProducts(q: string): Promise<Product[]> {
    const response = await fetch(`${API_BASE_URL}/search?q=${encodeURIComponent(q)}`);
    if (!response.ok) throw new Error("Failed to search products");
    const data = await response.json();
    return data.products;
}

export async function calculateListTotals(items: ListItem[]): Promise<StoreTotal[]> {
    const response = await fetch(`${API_BASE_URL}/list-totals`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ items })
    });
    if (!response.ok) throw new Error("Failed to calculate totals");
    const data = await response.json();
    return data.totals;
}

export async function fetchPriceHistory(productId: string): Promise<{ product: Product; history: any[] }> {
    const response = await fetch(`${API_BASE_URL}/price-history?productId=${productId}`);
    if (!response.ok) throw new Error("Failed to fetch price history");
    return response.json();
}
