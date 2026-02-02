import { render, screen } from "@testing-library/react";
import { HomePage } from "../pages/HomePage";
import { BrowserRouter } from "react-router-dom";

describe("HomePage", () => {
    it("renders the home dashboard correctly", async () => {
        render(
            <BrowserRouter>
                <HomePage />
            </BrowserRouter>
        );

        expect(await screen.findByText("Panel de Control")).toBeInTheDocument();
        expect(screen.getByText("Tiendas disponibles")).toBeInTheDocument();
    });

    it("contains summary cards", async () => {
        render(
            <BrowserRouter>
                <HomePage />
            </BrowserRouter>
        );

        expect(screen.getByText("Ahorro potencial")).toBeInTheDocument();
        expect(screen.getByText("Alertas")).toBeInTheDocument();
    });
});
