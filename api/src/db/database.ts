import Database from "better-sqlite3";
import { mkdirSync } from "node:fs";
import { dirname } from "node:path";
import { CREATE_TABLES_SQL } from "./schema.js";
import type { Store, Product, PriceSnapshot } from "../../../packages/contracts/src/types.js";

let db: Database.Database | null = null;

export function getDb(): Database.Database {
    if (!db) {
        const dbPath = process.env.DB_PATH || "./data/prices.db";
        mkdirSync(dirname(dbPath), { recursive: true });
        db = new Database(dbPath);
        db.pragma("journal_mode = WAL");
        db.pragma("foreign_keys = ON");
        db.exec(CREATE_TABLES_SQL);
    }
    return db;
}

export function upsertStore(store: {
    id: string;
    name: string;
    logoUrl: string | null;
    currency: string;
    region: string | null;
    state: string | null;
    municipality: string | null;
    address: string | null;
}): void {
    const database = getDb();
    const now = new Date().toISOString();
    database.prepare(`
        INSERT INTO stores (id, name, logo_url, currency, region, state, municipality, address, updated_at)
        VALUES (@id, @name, @logoUrl, @currency, @region, @state, @municipality, @address, @now)
        ON CONFLICT(id) DO UPDATE SET
            name = excluded.name,
            logo_url = excluded.logo_url,
            currency = excluded.currency,
            region = excluded.region,
            state = excluded.state,
            municipality = excluded.municipality,
            address = excluded.address,
            updated_at = excluded.updated_at
    `).run({ ...store, now });
}

export function upsertProduct(product: {
    id: string;
    name: string;
    brand: string | null;
    sizeLabel: string | null;
    imageUrl: string | null;
    category: string | null;
}): void {
    const database = getDb();
    const now = new Date().toISOString();
    database.prepare(`
        INSERT INTO products (id, name, brand, size_label, image_url, category, updated_at)
        VALUES (@id, @name, @brand, @sizeLabel, @imageUrl, @category, @now)
        ON CONFLICT(id) DO UPDATE SET
            name = excluded.name,
            brand = excluded.brand,
            size_label = excluded.size_label,
            image_url = excluded.image_url,
            category = excluded.category,
            updated_at = excluded.updated_at
    `).run({ ...product, now });
}

export function insertPriceSnapshot(snapshot: {
    productId: string;
    storeId: string;
    price: number;
    capturedAt: string;
    isPromo: boolean;
}): void {
    const database = getDb();
    database.prepare(`
        INSERT INTO price_snapshots (product_id, store_id, price, captured_at, is_promo)
        VALUES (@productId, @storeId, @price, @capturedAt, @isPromo)
    `).run({ ...snapshot, isPromo: snapshot.isPromo ? 1 : 0 });
}

export function queryStores(state?: string, limit = 20, offset = 0): Store[] {
    const database = getDb();
    let sql: string;
    let params: Record<string, unknown>;

    if (state) {
        sql = `SELECT id, name, logo_url, currency, region, state, municipality FROM stores WHERE state = @state ORDER BY name LIMIT @limit OFFSET @offset`;
        params = { state: state.toUpperCase(), limit, offset };
    } else {
        sql = `SELECT id, name, logo_url, currency, region, state, municipality FROM stores ORDER BY name LIMIT @limit OFFSET @offset`;
        params = { limit, offset };
    }

    const rows = database.prepare(sql).all(params) as Array<{
        id: string;
        name: string;
        logo_url: string | null;
        currency: string;
        region: string | null;
        state: string | null;
        municipality: string | null;
    }>;

    return rows.map(r => ({
        id: r.id,
        name: r.name,
        logoUrl: r.logo_url,
        currency: r.currency,
        region: r.region,
        state: r.state,
        municipality: r.municipality,
    }));
}

export function countStores(state?: string): number {
    const database = getDb();
    if (state) {
        return (database.prepare(`SELECT COUNT(*) as cnt FROM stores WHERE state = ?`)
            .get(state.toUpperCase()) as { cnt: number }).cnt;
    }
    return (database.prepare(`SELECT COUNT(*) as cnt FROM stores`).get() as { cnt: number }).cnt;
}

export function queryProducts(q: string, state?: string, limit = 20, offset = 0): Product[] {
    const database = getDb();
    const search = `%${q.toLowerCase()}%`;

    let sql: string;
    let params: Record<string, unknown>;

    if (state) {
        // Filter products to those that have prices in stores in the given state
        sql = `
            SELECT DISTINCT p.id, p.name, p.brand, p.size_label, p.image_url, p.category
            FROM products p
            INNER JOIN price_snapshots ps ON ps.product_id = p.id
            INNER JOIN stores s ON s.id = ps.store_id
            WHERE s.state = @state
              AND (LOWER(p.name) LIKE @search OR LOWER(COALESCE(p.brand, '')) LIKE @search)
            ORDER BY p.name
            LIMIT @limit OFFSET @offset
        `;
        params = { state: state.toUpperCase(), search, limit, offset };
    } else {
        sql = `
            SELECT id, name, brand, size_label, image_url, category
            FROM products
            WHERE LOWER(name) LIKE @search OR LOWER(COALESCE(brand, '')) LIKE @search
            ORDER BY name
            LIMIT @limit OFFSET @offset
        `;
        params = { search, limit, offset };
    }

    const rows = database.prepare(sql).all(params) as Array<{
        id: string;
        name: string;
        brand: string | null;
        size_label: string | null;
        image_url: string | null;
        category: string | null;
    }>;

    return rows.map(r => ({
        id: r.id,
        name: r.name,
        brand: r.brand,
        sizeLabel: r.size_label,
        imageUrl: r.image_url,
        category: r.category,
    }));
}

export function countProducts(q: string, state?: string): number {
    const database = getDb();
    const search = `%${q.toLowerCase()}%`;

    if (state) {
        return (database.prepare(`
            SELECT COUNT(DISTINCT p.id) as cnt
            FROM products p
            INNER JOIN price_snapshots ps ON ps.product_id = p.id
            INNER JOIN stores s ON s.id = ps.store_id
            WHERE s.state = @state
              AND (LOWER(p.name) LIKE @search OR LOWER(COALESCE(p.brand, '')) LIKE @search)
        `).get({ state: state.toUpperCase(), search }) as { cnt: number }).cnt;
    }

    return (database.prepare(`
        SELECT COUNT(*) as cnt FROM products
        WHERE LOWER(name) LIKE @search OR LOWER(COALESCE(brand, '')) LIKE @search
    `).get({ search }) as { cnt: number }).cnt;
}

export function queryProduct(id: string): Product | null {
    const database = getDb();
    const row = database.prepare(`
        SELECT id, name, brand, size_label, image_url, category FROM products WHERE id = ?
    `).get(id) as {
        id: string;
        name: string;
        brand: string | null;
        size_label: string | null;
        image_url: string | null;
        category: string | null;
    } | undefined;

    if (!row) return null;

    return {
        id: row.id,
        name: row.name,
        brand: row.brand,
        sizeLabel: row.size_label,
        imageUrl: row.image_url,
        category: row.category,
    };
}

export function queryPriceHistory(productId: string): Array<Pick<PriceSnapshot, "capturedAt" | "price" | "isPromo">> {
    const database = getDb();
    const rows = database.prepare(`
        SELECT captured_at, price, is_promo
        FROM price_snapshots
        WHERE product_id = ?
        ORDER BY captured_at DESC
        LIMIT 100
    `).all(productId) as Array<{ captured_at: string; price: number; is_promo: number }>;

    return rows.map(r => ({
        capturedAt: r.captured_at,
        price: r.price,
        isPromo: r.is_promo === 1,
    }));
}

export function queryLatestPriceForProductStore(productId: string, storeId: string): PriceSnapshot | null {
    const database = getDb();
    const row = database.prepare(`
        SELECT product_id, store_id, price, captured_at, is_promo
        FROM price_snapshots
        WHERE product_id = ? AND store_id = ?
        ORDER BY captured_at DESC
        LIMIT 1
    `).get(productId, storeId) as {
        product_id: string;
        store_id: string;
        price: number;
        captured_at: string;
        is_promo: number;
    } | undefined;

    if (!row) return null;

    return {
        productId: row.product_id,
        storeId: row.store_id,
        price: row.price,
        capturedAt: row.captured_at,
        isPromo: row.is_promo === 1,
    };
}

export function isDbEmpty(): boolean {
    try {
        const database = getDb();
        const result = database.prepare("SELECT COUNT(*) as cnt FROM stores").get() as { cnt: number };
        return result.cnt === 0;
    } catch {
        return true;
    }
}
