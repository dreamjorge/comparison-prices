import "@testing-library/jest-dom";
import { vi } from "vitest";

const fetchMock = vi.fn(() =>
  Promise.resolve({
    ok: true,
    json: () => Promise.resolve({ stores: [], products: [], totals: [] }),
  } as Response)
);

vi.stubGlobal("fetch", fetchMock);
