from fastapi.testclient import TestClient

from main import API_KEY, app

client = TestClient(app)

HEADERS = {"X-API-Key": API_KEY}


class TestStores:
    def test_list_stores_success(self):
        response = client.get("/stores", headers=HEADERS)
        assert response.status_code == 200
        data = response.json()
        assert "stores" in data
        assert len(data["stores"]) >= 3

    def test_list_stores_missing_key(self):
        response = client.get("/stores")
        assert response.status_code == 422

    def test_list_stores_invalid_key(self):
        response = client.get("/stores", headers={"X-API-Key": "wrong-key"})
        assert response.status_code == 403


class TestSearch:
    def test_search_products(self):
        response = client.get("/search?q=leche", headers=HEADERS)
        assert response.status_code == 200
        data = response.json()
        assert "products" in data
        assert len(data["products"]) >= 1

    def test_search_empty_results(self):
        response = client.get("/search?q=xyznonexistent", headers=HEADERS)
        assert response.status_code == 200
        data = response.json()
        assert data["products"] == []

    def test_search_pagination(self):
        response = client.get("/search?q=producto&limit=5", headers=HEADERS)
        assert response.status_code == 200
        data = response.json()
        assert len(data["products"]) <= 5

    def test_search_invalid_key(self):
        response = client.get("/search?q=leche", headers={"X-API-Key": "bad"})
        assert response.status_code == 403


class TestListTotals:
    def test_calculate_totals(self):
        payload = {"items": [{"productId": "p1", "quantity": 2}]}
        response = client.post("/list-totals", json=payload, headers=HEADERS)
        assert response.status_code == 200
        data = response.json()
        assert "totals" in data
        assert len(data["totals"]) == 3
        # Chedraui should be cheapest (0.9x)
        totals_by_store = {t["storeId"]: t["total"] for t in data["totals"]}
        assert totals_by_store["2"] < totals_by_store["1"]
        assert totals_by_store["1"] < totals_by_store["3"]

    def test_calculate_totals_invalid_key(self):
        payload = {"items": [{"productId": "p1", "quantity": 1}]}
        response = client.post("/list-totals", json=payload, headers={"X-API-Key": "bad"})
        assert response.status_code == 403


class TestPriceHistory:
    def test_get_price_history(self):
        response = client.get("/price-history?productId=p1")
        assert response.status_code == 200
        data = response.json()
        assert "product" in data
        assert "history" in data
        assert len(data["history"]) >= 1
