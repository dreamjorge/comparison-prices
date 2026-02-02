import { render, screen } from "@testing-library/react";
import { HomePage } from "../pages/HomePage";
import { BrowserRouter } from "react-router-dom";

describe("HomePage", () => {
    it("renders the home dashboard correctly", () => {
        render(
            <BrowserRouter>
                <HomePage />
            </BrowserRouter>
        );

        expect(screen.getByText("Resumen rápido")).toBeInTheDocument();
        expect(screen.getByText("Tiendas disponibles")).toBeInTheDocument();
    });

    it("contains summary cards", () => {
        render(
            <BrowserRouter>
                <HomePage />
            </BrowserRouter>
        );

        expect(screen.getByText("Ahorro potencial")).toBeInTheDocument();
        expect(screen.getByText("Alertas")).toBeInTheDocument();
    });
});
