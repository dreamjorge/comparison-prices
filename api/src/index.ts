import express from "express";
import cors from "cors";
import dotenv from "dotenv";
import { PRODUCTS, STORES, PRICE_SNAPSHOTS } from "./mockData.js";

dotenv.config();

const app = express();
const port = process.env.PORT || 4000;

app.use(cors({
    origin: ["http://localhost:4173", "http://127.0.0.1:4173"],
    methods: ["GET", "POST", "PUT", "DELETE"],
    allowedHeaders: ["Content-Type", "Authorization"]
}));
app.use(express.json());

// GET /v1/stores
app.get("/v1/stores", (req, res) => {
    res.json({ stores: STORES });
});

// GET /v1/search
app.get("/v1/search", (req, res) => {
    const q = (req.query.q as string || "").toLowerCase();
    const limit = parseInt(req.query.limit as string) || 20;
    const cursor = parseInt(req.query.cursor as string) || 0;

    const filtered = PRODUCTS.filter(p =>
        p.name.toLowerCase().includes(q) ||
        p.brand?.toLowerCase().includes(q)
    );

    const paginated = filtered.slice(cursor, cursor + limit);
    const nextCursor = (cursor + limit < filtered.length) ? (cursor + limit).toString() : null;

    res.json({
        products: paginated,
        nextCursor
    });
});

// GET /v1/price-history
app.get("/v1/price-history", (req, res) => {
    const productId = req.query.productId as string;
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

    res.json({ product, history });
});

// POST /v1/list-totals
app.post("/v1/list-totals", (req, res) => {
    const { items } = req.body;

    if (!items || !Array.isArray(items)) {
        return res.status(400).json({ error: "Invalid items" });
    }

    const totals = STORES.map(store => {
        let total = 0;
        items.forEach((item: { productId: string; quantity: number }) => {
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

    // Calculate savings vs next cheapest (optional for now, but good to have)
    const sortedTotals = [...totals].sort((a, b) => a.total - b.total);
    const resultWithSavings = totals.map(t => {
        const isCheapest = t.total === sortedTotals[0].total;
        let savings = null;
        if (isCheapest && sortedTotals.length > 1) {
            savings = parseFloat((sortedTotals[1].total - t.total).toFixed(2));
        }
        return { ...t, savings };
    });

    res.json({ totals: resultWithSavings });
});

app.listen(port, () => {
    console.log(`🚀 API ready at http://localhost:${port}/v1`);
});
