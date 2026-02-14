import { PriceSnapshot, Product, Store } from "../../../packages/contracts/src/types";
import { ProviderAdapter, ProviderProductMatch, ProviderSearchParams } from "./types.js";
import { createGoogleShoppingUrl, normalizedProductKey, normalizeText } from "./utils.js";

const SOURCE = "retailer_a_feed";
const sourceCapturedAt = new Date().toISOString();

const STORES: Store[] = [
  { id: "s1", name: "Walmart", logoUrl: null, currency: "MXN", region: "Centro" },
  { id: "s2", name: "Soriana", logoUrl: null, currency: "MXN", region: "Centro" }
];

const PRODUCTS: Product[] = [
  { id: "p1", name: "Leche entera", brand: "La Serenisima", sizeLabel: "1L", imageUrl: null, category: "Lácteos", sourceHints: ["licensed_feed"], externalUrl: null },
  { id: "p2", name: "Pan integral", brand: "Bimbo", sizeLabel: "600g", imageUrl: null, category: "Panadería", sourceHints: ["licensed_feed"], externalUrl: null },
  { id: "p3", name: "Arroz largo fino", brand: "Gallo", sizeLabel: "1kg", imageUrl: null, category: "Despensa", sourceHints: ["licensed_feed"], externalUrl: null },
  { id: "p5", name: "Detergente Ariel", brand: "Ariel", sizeLabel: "3kg", imageUrl: null, category: "Limpieza", sourceHints: ["licensed_feed"], externalUrl: null }
];

const SNAPSHOTS: PriceSnapshot[] = [
  { productId: "p1", storeId: "s1", price: 24.5, capturedAt: sourceCapturedAt, source: SOURCE, sourceCapturedAt, isPromo: false },
  { productId: "p2", storeId: "s1", price: 45, capturedAt: sourceCapturedAt, source: SOURCE, sourceCapturedAt, isPromo: false },
  { productId: "p3", storeId: "s1", price: 32, capturedAt: sourceCapturedAt, source: SOURCE, sourceCapturedAt, isPromo: false },
  { productId: "p5", storeId: "s1", price: 150, capturedAt: sourceCapturedAt, source: SOURCE, sourceCapturedAt, isPromo: false },
  { productId: "p1", storeId: "s2", price: 26, capturedAt: sourceCapturedAt, source: SOURCE, sourceCapturedAt, isPromo: false },
  { productId: "p2", storeId: "s2", price: 42.5, capturedAt: sourceCapturedAt, source: SOURCE, sourceCapturedAt, isPromo: true },
  { productId: "p3", storeId: "s2", price: 31, capturedAt: sourceCapturedAt, source: SOURCE, sourceCapturedAt, isPromo: false },
  { productId: "p5", storeId: "s2", price: 145, capturedAt: sourceCapturedAt, source: SOURCE, sourceCapturedAt, isPromo: false }
];

function searchProducts(params: ProviderSearchParams): ProviderProductMatch[] {
  const query = normalizeText(params.q);
  return PRODUCTS
    .filter((product) => normalizeText(`${product.brand ?? ""} ${product.name}`).includes(query))
    .map((product) => ({
      product: params.includeExternalLinks
        ? { ...product, externalUrl: createGoogleShoppingUrl(product) }
        : { ...product, externalUrl: null },
      normalizedKey: normalizedProductKey(product),
      source: SOURCE,
      sourceCapturedAt
    }));
}

export const retailerAProvider: ProviderAdapter = {
  id: SOURCE,
  listStores: () => STORES,
  searchProducts,
  getPriceSnapshots: () => SNAPSHOTS
};
