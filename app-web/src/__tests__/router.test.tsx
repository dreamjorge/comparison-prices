import { render, screen } from "@testing-library/react";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import { routesForTest } from "../test-utils/routesForTest";

describe("App routing", () => {
  it("renders the home route by default", () => {
    const router = createMemoryRouter(routesForTest, { initialEntries: ["/"] });
    render(<RouterProvider router={router} />);

    expect(screen.getByText("Resumen rápido")).toBeInTheDocument();
    expect(screen.getByText("Lista activa")).toBeInTheDocument();
  });

  it("renders the compare route", () => {
    const router = createMemoryRouter(routesForTest, {
      initialEntries: ["/comparador"]
    });
    render(<RouterProvider router={router} />);

    expect(screen.getByText("Comparador de tiendas")).toBeInTheDocument();
  });
});
