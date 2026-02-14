import { Store, Product, PriceSnapshot } from "../../packages/contracts/src/types";

export const STORES: Store[] = [
    { id: "s1", name: "Walmart", logoUrl: null, currency: "MXN", region: "Norte" },
    { id: "s2", name: "Soriana", logoUrl: null, currency: "MXN", region: "Norte" },
    { id: "s3", name: "Chedraui", logoUrl: null, currency: "MXN", region: "Norte" }
];

export const PRODUCTS: Product[] = [
    { id: "p1", name: "Leche entera", brand: "La Serenisima", sizeLabel: "1L", imageUrl: null, category: "Lácteos", externalUrl: null, sourceHints: ["licensed_feed"] },
    { id: "p2", name: "Pan integral", brand: "Bimbo", sizeLabel: "600g", imageUrl: null, category: "Panadería", externalUrl: null, sourceHints: ["licensed_feed"] },
    { id: "p3", name: "Arroz largo fino", brand: "Gallo", sizeLabel: "1kg", imageUrl: null, category: "Despensa", externalUrl: null, sourceHints: ["licensed_feed"] },
    { id: "p4", name: "Cerveza Corona", brand: "Corona", sizeLabel: "6 pack", imageUrl: null, category: "Bebidas", externalUrl: null, sourceHints: ["licensed_feed"] },
    { id: "p5", name: "Detergente Ariel", brand: "Ariel", sizeLabel: "3kg", imageUrl: null, category: "Limpieza", externalUrl: null, sourceHints: ["licensed_feed"] }
];

const nowIso = new Date().toISOString();

export const PRICE_SNAPSHOTS: PriceSnapshot[] = [
    // Leche
    { productId: "p1", storeId: "s1", price: 24.50, capturedAt: nowIso, source: "mock_feed", sourceCapturedAt: nowIso, isPromo: false },
    { productId: "p1", storeId: "s2", price: 26.00, capturedAt: nowIso, source: "mock_feed", sourceCapturedAt: nowIso, isPromo: false },
    { productId: "p1", storeId: "s3", price: 23.90, capturedAt: nowIso, source: "mock_feed", sourceCapturedAt: nowIso, isPromo: true },

    // Pan
    { productId: "p2", storeId: "s1", price: 45.00, capturedAt: nowIso, source: "mock_feed", sourceCapturedAt: nowIso, isPromo: false },
    { productId: "p2", storeId: "s2", price: 42.50, capturedAt: nowIso, source: "mock_feed", sourceCapturedAt: nowIso, isPromo: true },
    { productId: "p2", storeId: "s3", price: 46.00, capturedAt: nowIso, source: "mock_feed", sourceCapturedAt: nowIso, isPromo: false },

    // Arroz
    { productId: "p3", storeId: "s1", price: 32.00, capturedAt: nowIso, source: "mock_feed", sourceCapturedAt: nowIso, isPromo: false },
    { productId: "p3", storeId: "s2", price: 31.00, capturedAt: nowIso, source: "mock_feed", sourceCapturedAt: nowIso, isPromo: false },
    { productId: "p3", storeId: "s3", price: 33.50, capturedAt: nowIso, source: "mock_feed", sourceCapturedAt: nowIso, isPromo: false },

    // Corona
    { productId: "p4", storeId: "s1", price: 110.00, capturedAt: nowIso, source: "mock_feed", sourceCapturedAt: nowIso, isPromo: false },
    { productId: "p4", storeId: "s2", price: 105.00, capturedAt: nowIso, source: "mock_feed", sourceCapturedAt: nowIso, isPromo: true },
    { productId: "p4", storeId: "s3", price: 115.00, capturedAt: nowIso, source: "mock_feed", sourceCapturedAt: nowIso, isPromo: false },

    // Ariel
    { productId: "p5", storeId: "s1", price: 150.00, capturedAt: nowIso, source: "mock_feed", sourceCapturedAt: nowIso, isPromo: false },
    { productId: "p5", storeId: "s2", price: 145.00, capturedAt: nowIso, source: "mock_feed", sourceCapturedAt: nowIso, isPromo: false },
    { productId: "p5", storeId: "s3", price: 155.00, capturedAt: nowIso, source: "mock_feed", sourceCapturedAt: nowIso, isPromo: false }
];
