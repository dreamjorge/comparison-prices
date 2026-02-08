export const CREATE_TABLES_SQL = `
CREATE TABLE IF NOT EXISTS stores (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    logo_url TEXT,
    currency TEXT NOT NULL DEFAULT 'MXN',
    region TEXT,
    state TEXT,
    municipality TEXT,
    address TEXT,
    updated_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    brand TEXT,
    size_label TEXT,
    image_url TEXT,
    category TEXT,
    updated_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS price_snapshots (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id TEXT NOT NULL,
    store_id TEXT NOT NULL,
    price REAL NOT NULL,
    captured_at TEXT NOT NULL,
    is_promo INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (store_id) REFERENCES stores(id)
);

CREATE INDEX IF NOT EXISTS idx_price_snapshots_product ON price_snapshots(product_id);
CREATE INDEX IF NOT EXISTS idx_price_snapshots_store ON price_snapshots(store_id);
CREATE INDEX IF NOT EXISTS idx_price_snapshots_captured ON price_snapshots(captured_at);
CREATE INDEX IF NOT EXISTS idx_stores_state ON stores(state);
CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);
`;
