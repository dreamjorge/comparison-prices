import { render, screen } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import { afterEach, vi } from "vitest";
import { ComparePage } from "../pages/ComparePage";

describe("ComparePage coverage and warnings", () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("renders coverage summary from API response", async () => {
    const fetchMock = vi.fn()
      .mockResolvedValueOnce({
        ok: true,
        json: async () => ({
          totals: [
            { storeId: "s1", total: 100, savings: 5, updatedAt: new Date().toISOString(), matchedItems: 2, source: "provider_blend" },
            { storeId: "s2", total: 105, savings: null, updatedAt: new Date().toISOString(), matchedItems: 1, source: "provider_blend" }
          ],
          coverage: { matchedItems: 2, unmatchedItems: 1 },
          warnings: ["No todos los productos tienen cobertura de precio en las tiendas disponibles."]
        })
      } as Response)
      .mockResolvedValueOnce({
        ok: true,
        json: async () => ({
          stores: [
            { id: "s1", name: "Walmart", currency: "MXN", logoUrl: null, region: "Centro" },
            { id: "s2", name: "Soriana", currency: "MXN", logoUrl: null, region: "Centro" }
          ]
        })
      } as Response);

    vi.stubGlobal("fetch", fetchMock);

    render(
      <BrowserRouter>
        <ComparePage />
      </BrowserRouter>
    );

    expect(await screen.findByText("Cobertura de lista")).toBeInTheDocument();
    expect(screen.getByText(/Sin cobertura: 1/)).toBeInTheDocument();
    expect(screen.getByText(/No todos los productos tienen cobertura/)).toBeInTheDocument();
  });
});
