import { render, screen } from "@testing-library/react";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import { routesForTest } from "../test-utils/routesForTest";

describe("App routing", () => {
  it("renders the home route by default", async () => {
    const router = createMemoryRouter(routesForTest, { initialEntries: ["/"] });
    render(<RouterProvider router={router} />);

    expect(await screen.findByText("Panel de Control")).toBeInTheDocument();
    expect(screen.getByText("Tiendas disponibles")).toBeInTheDocument();
  });

  it("renders the compare route", () => {
    const router = createMemoryRouter(routesForTest, {
      initialEntries: ["/comparador"]
    });
    render(<RouterProvider router={router} />);

    expect(screen.getByText("Comparador de tiendas")).toBeInTheDocument();
  });
});
