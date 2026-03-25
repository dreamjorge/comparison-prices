import Database from "better-sqlite3";

const db = new Database("./data/prices.db");

console.log("📊 Database Statistics:");
console.log("======================");

const storeCount = db.prepare("SELECT COUNT(*) as count FROM stores").get();
console.log(`Stores: ${storeCount.count}`);

const productCount = db.prepare("SELECT COUNT(*) as count FROM products").get();
console.log(`Products: ${productCount.count}`);

const priceCount = db.prepare("SELECT COUNT(*) as count FROM price_snapshots").get();
console.log(`Price Snapshots: ${priceCount.count}`);

console.log("\n📍 Sample Stores:");
console.log("=================");
const stores = db.prepare("SELECT name, state, municipality FROM stores LIMIT 5").all();
stores.forEach((s, i) => console.log(`${i + 1}. ${s.name} - ${s.municipality}, ${s.state}`));

console.log("\n📦 Sample Products:");
console.log("===================");
const products = db.prepare("SELECT name, brand, size_label, category FROM products LIMIT 5").all();
products.forEach((p, i) => console.log(`${i + 1}. ${p.name} (${p.brand || 'No brand'}) - ${p.size_label || 'No size'} [${p.category || 'No category'}]`));

console.log("\n💰 Sample Prices:");
console.log("=================");
const prices = db.prepare(`
  SELECT p.name, s.name as store, ps.price, ps.captured_at
  FROM price_snapshots ps
  JOIN products p ON p.id = ps.product_id
  JOIN stores s ON s.id = ps.store_id
  LIMIT 5
`).all();
prices.forEach((pr, i) => console.log(`${i + 1}. ${pr.name} @ ${pr.store}: $${pr.price} MXN (${pr.captured_at.substring(0, 10)})`));

db.close();
