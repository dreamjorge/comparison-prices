import { beforeEach, describe, expect, it, vi } from "vitest";
import {
  calculateListTotals,
  fetchPriceHistory,
  fetchStores,
  searchProducts,
} from "./client";

describe("api client", () => {
  const fetchMock = vi.fn();

  beforeEach(() => {
    fetchMock.mockReset();
    vi.stubGlobal("fetch", fetchMock);
  });

  it("sends API key when fetching stores", async () => {
    fetchMock.mockResolvedValue({
      ok: true,
      json: async () => ({ stores: [] }),
    } as Response);

    await fetchStores();

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/v1/stores",
      expect.objectContaining({
        headers: expect.objectContaining({ "X-API-Key": "dummy-dev-key" }),
      })
    );
  });

  it("encodes search query", async () => {
    fetchMock.mockResolvedValue({
      ok: true,
      json: async () => ({ products: [] }),
    } as Response);

    await searchProducts("leche entera");

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/v1/search?q=leche%20entera",
      expect.any(Object)
    );
  });

  it("posts list totals payload", async () => {
    fetchMock.mockResolvedValue({
      ok: true,
      json: async () => ({ totals: [] }),
    } as Response);

    await calculateListTotals([{ productId: "p1", quantity: 2 }]);

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/v1/list-totals",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({ items: [{ productId: "p1", quantity: 2 }] }),
      })
    );
  });

  it("requests price history by product id", async () => {
    fetchMock.mockResolvedValue({
      ok: true,
      json: async () => ({
        product: { id: "p1", name: "Leche", brand: null, sizeLabel: null, imageUrl: null, category: null },
        history: [],
      }),
    } as Response);

    await fetchPriceHistory("p1");

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/v1/price-history?productId=p1",
      expect.any(Object)
    );
  });
});
