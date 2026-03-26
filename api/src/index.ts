import express from "express";
import cors from "cors";
import dotenv from "dotenv";
import { PRODUCTS, STORES, PRICE_SNAPSHOTS } from "./mockData.js";
import { fileURLToPath } from "node:url";
import { startDataRefresh, isDbEmpty } from "./services/dataSync.js";
import {
    queryStores, countStores,
    queryProducts, countProducts,
    queryProduct, queryPriceHistory,
    queryLatestPriceForProductStore,
} from "./db/database.js";

dotenv.config();

const port = process.env.PORT || 4000;
const apiKey = process.env.API_KEY || "dummy-dev-key";
const allowedOrigins = (process.env.CORS_ORIGINS || "http://localhost:4173,http://127.0.0.1:4173")
    .split(",")
    .map((origin) => origin.trim())
    .filter(Boolean);

import { ListItem } from "../../packages/contracts/src/types.js";

function requireApiKey(req: express.Request, res: express.Response, next: express.NextFunction) {
    const requestApiKey = req.header("X-API-Key");
    if (!requestApiKey || requestApiKey !== apiKey) {
        return res.status(403).json({ error: "Invalid API key" });
    }
    next();
}

function decodeCursor(cursor: string | undefined): number | null {
    if (!cursor) {
        return 0;
    }

    try {
        const decoded = Buffer.from(cursor, "base64").toString("utf8");
        const offset = Number.parseInt(decoded, 10);
        if (Number.isNaN(offset) || offset < 0) {
            return null;
        }
        return offset;
    } catch {
        return null;
    }
}

function encodeCursor(offset: number): string {
    return Buffer.from(String(offset), "utf8").toString("base64");
}

function normalizeStateParam(state: string | undefined): string | undefined {
    if (!state) return undefined;
    return state
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .trim() || undefined;
}

export function createApp() {
    const app = express();

    app.use(cors({
        origin: allowedOrigins,
        methods: ["GET", "POST"],
        allowedHeaders: ["Content-Type", "Authorization", "X-API-Key"]
    }));
    app.use(express.json());
    app.use("/v1", requireApiKey);

    // GET /v1/stores
    app.get("/v1/stores", (req, res) => {
        const limit = Math.min(Math.max(Number.parseInt(req.query.limit as string, 10) || 20, 1), 100);
        const offset = decodeCursor(req.query.cursor as string | undefined);
        if (offset === null) {
            return res.status(400).json({ error: "Invalid cursor" });
        }

        const state = normalizeStateParam(req.query.state as string | undefined);

        if (isDbEmpty()) {
            // Fallback to mock data
            const filtered = state ? STORES.filter(s => s.state?.toLowerCase() === state) : STORES;
            const stores = filtered.slice(offset, offset + limit);
            const nextCursor = offset + limit < filtered.length ? encodeCursor(offset + limit) : null;
            return res.json({ stores, nextCursor });
        }

        const stores = queryStores(state, limit, offset);
        const total = countStores(state);
        const nextCursor = offset + limit < total ? encodeCursor(offset + limit) : null;
        res.json({ stores, nextCursor });
    });

    // GET /v1/search
    app.get("/v1/search", (req, res) => {
        const q = (req.query.q as string || "").toLowerCase();
        const limit = Math.min(Math.max(Number.parseInt(req.query.limit as string, 10) || 20, 1), 100);
        const offset = decodeCursor(req.query.cursor as string | undefined);
        if (offset === null) {
            return res.status(400).json({ error: "Invalid cursor" });
        }

        const state = normalizeStateParam(req.query.state as string | undefined);

        if (isDbEmpty()) {
            // Fallback to mock data
            const filtered = PRODUCTS.filter(p =>
                p.name.toLowerCase().includes(q) ||
                p.brand?.toLowerCase().includes(q)
            );
            const paginated = filtered.slice(offset, offset + limit);
            const nextCursor = (offset + limit < filtered.length) ? encodeCursor(offset + limit) : null;
            return res.json({ products: paginated, nextCursor });
        }

        const products = queryProducts(q, state, limit, offset);
        const total = countProducts(q, state);
        const nextCursor = (offset + limit < total) ? encodeCursor(offset + limit) : null;

        res.json({ products, nextCursor });
    });

    // GET /v1/price-history
    app.get("/v1/price-history", (req, res) => {
        const productId = req.query.productId as string;

        if (isDbEmpty()) {
            const product = PRODUCTS.find(p => p.id === productId);
            if (!product) {
                return res.status(404).json({ error: "Product not found" });
            }
            const history = PRICE_SNAPSHOTS
                .filter(s => s.productId === productId)
                .map(s => ({
                    capturedAt: s.capturedAt,
                    price: s.price,
                    isPromo: s.isPromo
                }));
            return res.json({ product, history });
        }

        const product = queryProduct(productId);
        if (!product) {
            return res.status(404).json({ error: "Product not found" });
        }

        const history = queryPriceHistory(productId);
        res.json({ product, history });
    });

    // POST /v1/list-totals
    app.post("/v1/list-totals", (req, res) => {
        const { items } = req.body;

        if (!items || !Array.isArray(items)) {
            return res.status(400).json({ error: "Invalid items" });
        }

        const hasInvalidItems = items.some(
            (item: ListItem) =>
                !item.productId ||
                typeof item.quantity !== "number" ||
                Number.isNaN(item.quantity) ||
                item.quantity <= 0
        );
        if (hasInvalidItems) {
            return res.status(400).json({ error: "Each item requires a productId and quantity > 0" });
        }

        if (isDbEmpty()) {
            const totals = STORES.map(store => {
                let total = 0;
                items.forEach((item: ListItem) => {
                    const snapshot = PRICE_SNAPSHOTS.find(
                        s => s.productId === item.productId && s.storeId === store.id
                    );
                    if (snapshot) {
                        total += snapshot.price * item.quantity;
                    }
                });

                return {
                    storeId: store.id,
                    total: parseFloat(total.toFixed(2)),
                    updatedAt: new Date().toISOString()
                };
            });

            const sortedTotals = [...totals].sort((a, b) => a.total - b.total);
            const resultWithSavings = totals.map(t => {
                const isCheapest = t.total === sortedTotals[0].total;
                let savings = null;
                if (isCheapest && sortedTotals.length > 1) {
                    savings = parseFloat((sortedTotals[1].total - t.total).toFixed(2));
                }
                return { ...t, savings };
            });

            return res.json({ totals: resultWithSavings });
        }

        // Get all stores from DB and calculate totals for each
        const allStores = queryStores(undefined, 100, 0);
        const totals = allStores
            .map(store => {
                let total = 0;
                const updatedAt = new Date().toISOString();
                items.forEach((item: ListItem) => {
                    const snapshot = queryLatestPriceForProductStore(item.productId, store.id);
                    if (snapshot) {
                        total += snapshot.price * item.quantity;
                    }
                });

                if (total === 0) return null;

                return {
                    storeId: store.id,
                    total: parseFloat(total.toFixed(2)),
                    updatedAt
                };
            })
            .filter((t): t is NonNullable<typeof t> => t !== null);

        const sortedTotals = [...totals].sort((a, b) => a.total - b.total);
        const resultWithSavings = totals.map(t => {
            const isCheapest = t.total === sortedTotals[0]?.total;
            let savings = null;
            if (isCheapest && sortedTotals.length > 1) {
                savings = parseFloat((sortedTotals[1].total - t.total).toFixed(2));
            }
            return { ...t, savings };
        });

        res.json({ totals: resultWithSavings });
    });

    return app;
}

const app = createApp();

const entrypoint = process.argv[1] ? fileURLToPath(import.meta.url) === process.argv[1] : false;
if (entrypoint) {
    startDataRefresh();
    app.listen(port, () => {
        console.log(`🚀 API ready at http://localhost:${port}/v1`);
    });
}
