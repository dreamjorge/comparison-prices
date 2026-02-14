export interface Store {
    id: string;
    name: string;
    logoUrl: string | null;
    currency: string;
    region: string | null;
}

export interface Product {
    id: string;
    name: string;
    brand: string | null;
    sizeLabel: string | null;
    imageUrl: string | null;
    category: string | null;
    externalUrl?: string | null;
    sourceHints?: string[] | null;
}

export interface PriceSnapshot {
    productId: string;
    storeId: string;
    price: number;
    capturedAt: string;
    source: string;
    sourceCapturedAt: string;
    isPromo: boolean;
}

export interface ListItem {
    productId: string;
    quantity: number;
    unitLabel?: string;
}

export interface StoreTotal {
    storeId: string;
    total: number;
    updatedAt: string;
    savings: number | null;
    matchedItems?: number;
    source?: string | null;
}

export interface CoverageSummary {
    matchedItems: number;
    unmatchedItems: number;
}
