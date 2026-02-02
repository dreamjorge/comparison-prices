import "@testing-library/jest-dom";
import { vi } from "vitest";

// Mock global fetch to handle relative URLs in tests
global.fetch = vi.fn(() =>
    Promise.resolve({
        ok: true,
        json: () => Promise.resolve({ stores: [], products: [], totals: [] }),
    } as Response)
);
