import { PriceSnapshot, Product, Store } from "../../../packages/contracts/src/types";
import { ProviderAdapter, ProviderProductMatch, ProviderSearchParams } from "./types.js";
import { createGoogleShoppingUrl, normalizedProductKey, normalizeText } from "./utils.js";

const SOURCE = "retailer_b_feed";
const sourceCapturedAt = new Date().toISOString();

const STORES: Store[] = [
  { id: "s3", name: "Chedraui", logoUrl: null, currency: "MXN", region: "Centro" },
  { id: "s4", name: "La Comer", logoUrl: null, currency: "MXN", region: "Centro" }
];

const PRODUCTS: Product[] = [
  { id: "p1", name: "Leche Entera", brand: "La Serenisima", sizeLabel: "1000ml", imageUrl: null, category: "Lácteos", sourceHints: ["licensed_feed"], externalUrl: null },
  { id: "p2", name: "Pan Integral", brand: "Bimbo", sizeLabel: "0.6kg", imageUrl: null, category: "Panadería", sourceHints: ["licensed_feed"], externalUrl: null },
  { id: "p3", name: "Arroz largo fino", brand: "Gallo", sizeLabel: "1kg", imageUrl: null, category: "Despensa", sourceHints: ["licensed_feed"], externalUrl: null },
  { id: "p4", name: "Cerveza Corona", brand: "Corona", sizeLabel: "6 pack", imageUrl: null, category: "Bebidas", sourceHints: ["licensed_feed"], externalUrl: null }
];

const SNAPSHOTS: PriceSnapshot[] = [
  { productId: "p1", storeId: "s3", price: 23.9, capturedAt: sourceCapturedAt, source: SOURCE, sourceCapturedAt, isPromo: true },
  { productId: "p2", storeId: "s3", price: 46, capturedAt: sourceCapturedAt, source: SOURCE, sourceCapturedAt, isPromo: false },
  { productId: "p3", storeId: "s3", price: 33.5, capturedAt: sourceCapturedAt, source: SOURCE, sourceCapturedAt, isPromo: false },
  { productId: "p4", storeId: "s3", price: 115, capturedAt: sourceCapturedAt, source: SOURCE, sourceCapturedAt, isPromo: false },
  { productId: "p1", storeId: "s4", price: 25.2, capturedAt: sourceCapturedAt, source: SOURCE, sourceCapturedAt, isPromo: false },
  { productId: "p2", storeId: "s4", price: 43.2, capturedAt: sourceCapturedAt, source: SOURCE, sourceCapturedAt, isPromo: false },
  { productId: "p3", storeId: "s4", price: 32.8, capturedAt: sourceCapturedAt, source: SOURCE, sourceCapturedAt, isPromo: false },
  { productId: "p4", storeId: "s4", price: 108, capturedAt: sourceCapturedAt, source: SOURCE, sourceCapturedAt, isPromo: true }
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

export const retailerBProvider: ProviderAdapter = {
  id: SOURCE,
  listStores: () => STORES,
  searchProducts,
  getPriceSnapshots: () => SNAPSHOTS
};
