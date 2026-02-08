import { PROFECOProvider } from "./dist/api/src/providers/PROFECOProvider.js";
import { upsertStore, upsertProduct, insertPriceSnapshot } from "./dist/api/src/db/database.js";

console.log("🔄 Testing PROFECO data fetch...\n");

const provider = new PROFECOProvider();

console.log("📡 Fetching prices from PROFECO...");
const records = await provider.fetchPrices();

console.log(`\n✅ Fetched ${records.length} records`);

if (records.length > 0) {
  console.log("\n📋 Sample records:");
  records.slice(0, 3).forEach((r, i) => {
    console.log(`${i + 1}. ${r.product} (${r.brand || 'No brand'}) @ ${r.store}`);
    console.log(`   Price: $${r.price} MXN | State: ${r.state} | Municipality: ${r.municipality}`);
  });

  console.log("\n💾 Syncing first 1000 records to database...");

  const storeCache = new Map();
  const productCache = new Map();

  function makeStoreId(storeName, state, municipality) {
    const slug = [storeName, state, municipality]
      .join("-")
      .toLowerCase()
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "")
      .replace(/[^a-z0-9]+/g, "-")
      .replace(/^-|-$/g, "");
    return `store-${slug}`;
  }

  function makeProductId(name, brand, presentation) {
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

  const recordsToSync = records.slice(0, 1000);

  for (const record of recordsToSync) {
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

  console.log(`✅ Synced ${recordsToSync.length} records to database`);
  console.log(`   Unique stores: ${storeCache.size}`);
  console.log(`   Unique products: ${productCache.size}`);
} else {
  console.log("\n⚠️  No records fetched. The PROFECO API might be unavailable or the data format changed.");
}
