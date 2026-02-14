import express from "express";
import cors from "cors";
import dotenv from "dotenv";
import { PRODUCTS } from "./mockData.js";
import { providerAggregator } from "./providers/index.js";

dotenv.config();

const app = express();
const port = process.env.PORT || 4000;
const RATE_LIMIT_WINDOW_MS = 60_000;
const RATE_LIMIT_MAX_REQUESTS = 60;
const SEARCH_CACHE_TTL_MS = 30_000;
const requestBuckets = new Map<string, { count: number; windowStart: number }>();
const searchCache = new Map<string, { expiresAt: number; payload: { products: unknown[]; nextCursor: string | null } }>();

import { ListItem } from "../../packages/contracts/src/types";

app.use(cors({
    origin: ["http://localhost:4173", "http://127.0.0.1:4173"],
    methods: ["GET", "POST", "PUT", "DELETE"],
    allowedHeaders: ["Content-Type", "Authorization"]
}));
app.use(express.json());

app.use((req, res, next) => {
    const key = (req.ip || req.socket.remoteAddress || "unknown").toString();
    const now = Date.now();
    const existing = requestBuckets.get(key);

    if (!existing || now - existing.windowStart > RATE_LIMIT_WINDOW_MS) {
        requestBuckets.set(key, { count: 1, windowStart: now });
        return next();
    }

    if (existing.count >= RATE_LIMIT_MAX_REQUESTS) {
        return res.status(429).json({ error: "Rate limit exceeded. Try again in a minute." });
    }

    existing.count += 1;
    requestBuckets.set(key, existing);
    return next();
});

// GET /v1/stores
app.get("/v1/stores", (req, res) => {
    res.json({ stores: providerAggregator.listStores() });
});

// GET /v1/search
app.get("/v1/search", (req, res) => {
    const q = (req.query.q as string || "").toLowerCase();
    const limit = parseInt(req.query.limit as string) || 20;
    const cursor = parseInt(req.query.cursor as string) || 0;
    const storeId = (req.query.storeId as string | undefined) || undefined;
    const zoneId = (req.query.zoneId as string | undefined) || undefined;
    const lat = req.query.lat ? Number(req.query.lat) : undefined;
    const lon = req.query.lon ? Number(req.query.lon) : undefined;
    const includeExternalLinks = req.query.includeExternalLinks === "true";

    const cacheKey = JSON.stringify({ q, limit, cursor, storeId, zoneId, lat, lon, includeExternalLinks });
    const cached = searchCache.get(cacheKey);
    if (cached && cached.expiresAt > Date.now()) {
        return res.json(cached.payload);
    }

    const startedAt = Date.now();
    const searchResult = providerAggregator.searchProducts({
        q,
        limit,
        cursor,
        storeId,
        zoneId,
        lat,
        lon,
        includeExternalLinks
    });

    res.json({
        products: searchResult.products,
        nextCursor: searchResult.nextCursor
    });
    searchCache.set(cacheKey, {
        expiresAt: Date.now() + SEARCH_CACHE_TTL_MS,
        payload: {
            products: searchResult.products,
            nextCursor: searchResult.nextCursor
        }
    });
    console.log(`[provider-search] q="${q}" zone="${zoneId ?? "-"}" includeLinks=${includeExternalLinks} count=${searchResult.products.length} latencyMs=${Date.now() - startedAt}`);
});

// GET /v1/price-history
app.get("/v1/price-history", (req, res) => {
    const productId = req.query.productId as string;
    const product = PRODUCTS.find(p => p.id === productId);

    if (!product) {
        return res.status(404).json({ error: "Product not found" });
    }

    const history = providerAggregator.getPriceHistory(productId);

    res.json({ product, history });
});

// POST /v1/list-totals
app.post("/v1/list-totals", (req, res) => {
    const { items } = req.body;

    if (!items || !Array.isArray(items)) {
        return res.status(400).json({ error: "Invalid items" });
    }

    const result = providerAggregator.calculateListTotals(items as ListItem[]);
    res.json(result);
});

app.listen(port, () => {
    console.log(`🚀 API ready at http://localhost:${port}/v1`);
});
