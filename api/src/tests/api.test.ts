import test from "node:test";
import assert from "node:assert/strict";
import type { AddressInfo } from "node:net";

import { createApp } from "../index.js";

type JsonValue = Record<string, unknown>;

async function withServer(run: (baseUrl: string) => Promise<void>) {
  const app = createApp();
  const server = app.listen(0);

  await new Promise<void>((resolve) => server.once("listening", () => resolve()));

  const { port } = server.address() as AddressInfo;
  const baseUrl = `http://127.0.0.1:${port}`;

  try {
    await run(baseUrl);
  } finally {
    await new Promise<void>((resolve, reject) => {
      server.close((error) => (error ? reject(error) : resolve()));
    });
  }
}

async function getJson(url: string, init?: RequestInit) {
  const response = await fetch(url, init);
  const data = (await response.json()) as JsonValue;
  return { response, data };
}

test("rejects missing API key", async () => {
  await withServer(async (baseUrl) => {
    const { response, data } = await getJson(`${baseUrl}/v1/stores`);
    assert.equal(response.status, 403);
    assert.equal(data.error, "Invalid API key");
  });
});

test("returns 400 on invalid cursor", async () => {
  await withServer(async (baseUrl) => {
    const { response, data } = await getJson(`${baseUrl}/v1/search?q=leche&cursor=not-base64`, {
      headers: { "X-API-Key": "dummy-dev-key" },
    });

    assert.equal(response.status, 400);
    assert.equal(data.error, "Invalid cursor");
  });
});

test("returns 400 for invalid list items", async () => {
  await withServer(async (baseUrl) => {
    const { response, data } = await getJson(`${baseUrl}/v1/list-totals`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-API-Key": "dummy-dev-key",
      },
      body: JSON.stringify({ items: [{ productId: "p1", quantity: 0 }] }),
    });

    assert.equal(response.status, 400);
    assert.equal(data.error, "Each item requires a productId and quantity > 0");
  });
});

test("returns paginated stores with nextCursor", async () => {
  await withServer(async (baseUrl) => {
    const { response, data } = await getJson(`${baseUrl}/v1/stores?limit=2`, {
      headers: { "X-API-Key": "dummy-dev-key" },
    });

    assert.equal(response.status, 200);
    assert.equal(Array.isArray(data.stores), true);
    assert.equal((data.stores as unknown[]).length, 2);
    assert.equal(typeof data.nextCursor, "string");
  });
});
