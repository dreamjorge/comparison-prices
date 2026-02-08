export interface PriceRecord {
    product: string;
    brand: string | null;
    presentation: string | null;
    category: string | null;
    store: string;
    address: string | null;
    price: number;
    state: string;
    municipality: string;
    capturedAt: string;
}

export interface DataProvider {
    getName(): string;
    fetchPrices(state?: string): Promise<PriceRecord[]>;
}
