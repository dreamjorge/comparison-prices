import { PROFECOProvider } from "../providers/PROFECOProvider.js";
import type { DataProvider, PriceRecord } from "../providers/DataProvider.js";
import { upsertStore, upsertProduct, insertPriceSnapshot, isDbEmpty } from "../db/database.js";

const providers: DataProvider[] = [
    new PROFECOProvider(),
    // new WalmartMXScraper(),  // ← future web scrapers go here
];

function makeStoreId(storeName: string, state: string, municipality: string): string {
    const slug = [storeName, state, municipality]
        .join("-")
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .replace(/[^a-z0-9]+/g, "-")
        .replace(/^-|-$/g, "");
    return `store-${slug}`;
}

function makeProductId(name: string, brand: string | null, presentation: string | null): string {
    const slug = [name, brand, presentation]
        .filter(Boolean)
        .join("-")
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .replace(/[^a-z0-9]+/g, "-")
        .replace(/^-|-$/g, "");
    return `prod-${slug}`;
}

async function syncProvider(provider: DataProvider): Promise<void> {
    console.log(`[dataSync] Fetching from ${provider.getName()}...`);
    const records = await provider.fetchPrices();
    console.log(`[dataSync] Got ${records.length} records from ${provider.getName()}`);

    if (records.length === 0) {
        console.warn(`[dataSync] No records from ${provider.getName()}, skipping`);
        return;
    }

    const storeCache = new Map<string, string>();
    const productCache = new Map<string, string>();

    for (const record of records) {
        // Upsert store
        const storeKey = `${record.store}|${record.state}|${record.municipality}`;
        let storeId = storeCache.get(storeKey);
        if (!storeId) {
            storeId = makeStoreId(record.store, record.state, record.municipality);
            storeCache.set(storeKey, storeId);
            upsertStore({
                id: storeId,
                name: record.store,
                logoUrl: null,
                currency: "MXN",
                region: record.state,
                state: record.state,
                municipality: record.municipality,
                address: record.address,
            });
        }

        // Upsert product
        const productKey = `${record.product}|${record.brand}|${record.presentation}`;
        let productId = productCache.get(productKey);
        if (!productId) {
            productId = makeProductId(record.product, record.brand, record.presentation);
            productCache.set(productKey, productId);
            upsertProduct({
                id: productId,
                name: record.product,
                brand: record.brand,
                sizeLabel: record.presentation,
                imageUrl: null,
                category: record.category,
            });
        }

        // Insert price snapshot
        insertPriceSnapshot({
            productId,
            storeId,
            price: record.price,
            capturedAt: record.capturedAt,
            isPromo: false,
        });
    }

    console.log(`[dataSync] Synced ${records.length} price records from ${provider.getName()}`);
}

export async function syncAllProviders(): Promise<void> {
    for (const provider of providers) {
        try {
            await syncProvider(provider);
        } catch (err) {
            console.error(`[dataSync] Error syncing ${provider.getName()}:`, err);
        }
    }
}

export function startDataRefresh(): void {
    const refreshHours = parseFloat(process.env.DATA_REFRESH_HOURS || "24");
    const refreshMs = refreshHours * 60 * 60 * 1000;

    // Initial sync on startup (non-blocking)
    syncAllProviders().catch(err => console.error("[dataSync] Initial sync failed:", err));

    // Schedule periodic refresh
    setInterval(() => {
        syncAllProviders().catch(err => console.error("[dataSync] Refresh failed:", err));
    }, refreshMs);

    console.log(`[dataSync] Data refresh scheduled every ${refreshHours}h`);
}

export { isDbEmpty };
