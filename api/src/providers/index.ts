import { ListItem, PriceSnapshot, Product, Store, StoreTotal } from "../../../packages/contracts/src/types";
import { retailerAProvider } from "./retailerA.js";
import { retailerBProvider } from "./retailerB.js";
import { ListTotalsResult, ProviderAdapter, ProviderAggregator, ProviderSearchParams } from "./types.js";

const STALE_HOURS_THRESHOLD = 72;
const DEFAULT_LIMIT = 20;
const PROVIDERS: ProviderAdapter[] = [retailerAProvider, retailerBProvider];

function dedupeStores(stores: Store[]): Store[] {
  const byId = new Map<string, Store>();
  stores.forEach((store) => {
    if (!byId.has(store.id)) {
      byId.set(store.id, store);
    }
  });
  return [...byId.values()];
}

function dedupeProducts(matches: ReturnType<ProviderAdapter["searchProducts"]>): Product[] {
  const byNormalizedKey = new Map<string, Product>();
  matches.forEach((match) => {
    const existing = byNormalizedKey.get(match.normalizedKey);
    if (!existing) {
      byNormalizedKey.set(match.normalizedKey, {
        ...match.product,
        sourceHints: [match.source]
      });
      return;
    }

    const mergedHints = new Set<string>([...(existing.sourceHints ?? []), match.source]);
    const externalUrl = existing.externalUrl ?? match.product.externalUrl ?? null;
    byNormalizedKey.set(match.normalizedKey, {
      ...existing,
      externalUrl,
      sourceHints: [...mergedHints]
    });
  });
  return [...byNormalizedKey.values()];
}

function findBestSnapshot(productId: string, storeId: string, snapshots: PriceSnapshot[]): PriceSnapshot | null {
  const candidates = snapshots.filter((snapshot) => snapshot.productId === productId && snapshot.storeId === storeId);
  if (candidates.length === 0) {
    return null;
  }
  return candidates.reduce((lowest, current) => (current.price < lowest.price ? current : lowest));
}

function calculateSavings(sortedTotals: StoreTotal[]): StoreTotal[] {
  return sortedTotals.map((storeTotal, index) => {
    const next = sortedTotals[index + 1];
    const savings = next ? Number((next.total - storeTotal.total).toFixed(2)) : null;
    return { ...storeTotal, savings };
  });
}

function buildWarnings(items: ListItem[], matchedItems: number, snapshots: PriceSnapshot[]): string[] {
  const warnings: string[] = [];
  if (matchedItems < items.length) {
    warnings.push("No todos los productos tienen cobertura de precio en las tiendas disponibles.");
  }

  const staleExists = snapshots.some((snapshot) => {
    const ageMs = Date.now() - new Date(snapshot.sourceCapturedAt).getTime();
    const ageHours = ageMs / (1000 * 60 * 60);
    return ageHours > STALE_HOURS_THRESHOLD;
  });
  if (staleExists) {
    warnings.push("Hay precios con antigüedad mayor a 72 horas.");
  }

  return warnings;
}

function calculateListTotals(items: ListItem[]): ListTotalsResult {
  const stores = dedupeStores(PROVIDERS.flatMap((provider) => provider.listStores()));
  const snapshots = PROVIDERS.flatMap((provider) => provider.getPriceSnapshots());

  const matchedItemIds = new Set<string>();
  const totals: StoreTotal[] = stores.map((store) => {
    let total = 0;
    let matchedItems = 0;

    items.forEach((item) => {
      const snapshot = findBestSnapshot(item.productId, store.id, snapshots);
      if (snapshot) {
        total += snapshot.price * item.quantity;
        matchedItems += 1;
        matchedItemIds.add(item.productId);
      }
    });

    return {
      storeId: store.id,
      total: Number(total.toFixed(2)),
      updatedAt: new Date().toISOString(),
      savings: null,
      matchedItems,
      source: "provider_blend"
    };
  });

  const rankedStores = totals
    .filter((storeTotal) => (storeTotal.matchedItems ?? 0) > 0)
    .sort((a, b) => a.total - b.total);
  const rankedWithSavings = calculateSavings(rankedStores);
  const savingsByStoreId = new Map(rankedWithSavings.map((storeTotal) => [storeTotal.storeId, storeTotal.savings]));
  const totalsWithSavings = totals
    .map((storeTotal) => ({
      ...storeTotal,
      savings: savingsByStoreId.get(storeTotal.storeId) ?? null
    }))
    .sort((a, b) => a.total - b.total);

  return {
    totals: totalsWithSavings,
    coverage: {
      matchedItems: matchedItemIds.size,
      unmatchedItems: Math.max(0, items.length - matchedItemIds.size)
    },
    warnings: buildWarnings(items, matchedItemIds.size, snapshots)
  };
}

function listStores(): Store[] {
  return dedupeStores(PROVIDERS.flatMap((provider) => provider.listStores()));
}

function searchProducts(params: ProviderSearchParams): { products: Product[]; nextCursor: string | null } {
  const limit = Number.isFinite(params.limit) ? params.limit : DEFAULT_LIMIT;
  const cursor = Number.isFinite(params.cursor) ? params.cursor : 0;
  const allSnapshots = PROVIDERS.flatMap((provider) => provider.getPriceSnapshots());
  const allMatches = PROVIDERS.flatMap((provider) => provider.searchProducts(params))
    .filter((match) => {
      if (!params.storeId) {
        return true;
      }
      return allSnapshots.some((snapshot) => snapshot.storeId === params.storeId && snapshot.productId === match.product.id);
    });
  const deduped = dedupeProducts(allMatches);
  const products = deduped.slice(cursor, cursor + limit);
  const nextCursor = cursor + limit < deduped.length ? String(cursor + limit) : null;
  return { products, nextCursor };
}

function getPriceHistory(productId: string): Array<Pick<PriceSnapshot, "capturedAt" | "price" | "isPromo">> {
  return PROVIDERS
    .flatMap((provider) => provider.getPriceSnapshots())
    .filter((snapshot) => snapshot.productId === productId)
    .map((snapshot) => ({
      capturedAt: snapshot.capturedAt,
      price: snapshot.price,
      isPromo: snapshot.isPromo
    }));
}

export const providerAggregator: ProviderAggregator = {
  listStores,
  searchProducts,
  calculateListTotals,
  getPriceHistory
};
