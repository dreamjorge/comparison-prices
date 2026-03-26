import { render, screen, waitFor } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import { afterEach, vi } from "vitest";
import { HomePage } from "../pages/HomePage";

describe("HomePage external links", () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("renders Google Shopping link when API returns externalUrl", async () => {
    const fetchMock = vi.fn()
      .mockResolvedValueOnce({
        ok: true,
        json: async () => ({ stores: [{ id: "s1", name: "Walmart", currency: "MXN", logoUrl: null, region: "Centro" }] })
      } as Response)
      .mockResolvedValueOnce({
        ok: true,
        json: async () => ({
          products: [
            {
              id: "p1",
              name: "Leche entera",
              brand: "La Serenisima",
              sizeLabel: "1L",
              imageUrl: null,
              category: "Lácteos",
              externalUrl: "https://www.google.com/search?tbm=shop&q=leche",
              sourceHints: ["retailer_a_feed"]
            }
          ]
        })
      } as Response);
    vi.stubGlobal("fetch", fetchMock);

    render(
      <BrowserRouter>
        <HomePage />
      </BrowserRouter>
    );

    expect(await screen.findByText("Panel de Control")).toBeInTheDocument();
    await waitFor(() => expect(screen.getByText("Ver en Google Shopping")).toBeInTheDocument());
    expect(screen.getByText(/Fuente:/)).toBeInTheDocument();
  });
});
