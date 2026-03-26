import { ListItem, PriceSnapshot, Product, Store, StoreTotal } from "../../../packages/contracts/src/types";

export interface ProviderSearchParams {
  q: string;
  storeId?: string;
  lat?: number;
  lon?: number;
  zoneId?: string;
  includeExternalLinks?: boolean;
  limit: number;
  cursor: number;
}

export interface ProviderProductMatch {
  product: Product;
  normalizedKey: string;
  source: string;
  sourceCapturedAt: string;
}

export interface ProviderAdapter {
  id: string;
  listStores(): Store[];
  searchProducts(params: ProviderSearchParams): ProviderProductMatch[];
  getPriceSnapshots(): PriceSnapshot[];
}

export interface ListTotalsResult {
  totals: StoreTotal[];
  coverage: {
    matchedItems: number;
    unmatchedItems: number;
  };
  warnings: string[];
}

export interface ProviderAggregator {
  listStores(): Store[];
  searchProducts(params: ProviderSearchParams): { products: Product[]; nextCursor: string | null };
  calculateListTotals(items: ListItem[]): ListTotalsResult;
  getPriceHistory(productId: string): Array<Pick<PriceSnapshot, "capturedAt" | "price" | "isPromo">>;
}
