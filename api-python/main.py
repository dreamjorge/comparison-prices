from fastapi import FastAPI, Query, Body, Header, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from datetime import datetime
from typing import List, Optional
import uuid
import base64
from services.normalization import NormalizationService

app = FastAPI(title="Comparison Prices API", version="0.1.0")

# Security key (Mock for MVP)
API_KEY = "dummy-dev-key"

# CORS Configuration - Restricted for production-like safety
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:4173"], # Only the web app
    allow_methods=["GET", "POST"],
    allow_headers=["*"],
)

def verify_api_key(x_api_key: str = Header(...)):
    if x_api_key != API_KEY:
        raise HTTPException(status_code=403, detail="Invalid API Key")
    return x_api_key

# --- Models (matching openapi.json) ---

class Store(BaseModel):
    id: str
    name: str
    logoUrl: Optional[str] = None
    currency: str = "MXN"
    region: Optional[str] = None

class StoreListResponse(BaseModel):
    stores: List[Store]

class Product(BaseModel):
    id: str
    name: str
    brand: Optional[str] = None
    sizeLabel: Optional[str] = None
    imageUrl: Optional[str] = None
    category: Optional[str] = None

class ProductSearchResponse(BaseModel):
    products: List[Product]
    nextCursor: Optional[str] = None

class ListItem(BaseModel):
    productId: str
    quantity: float
    unitLabel: Optional[str] = None

class ListTotalsRequest(BaseModel):
    items: List[ListItem]

class StoreTotal(BaseModel):
    storeId: str
    total: float
    savings: Optional[float] = None
    updatedAt: Optional[datetime] = None

class ListTotalsResponse(BaseModel):
    totals: List[StoreTotal]

class PriceHistoryPoint(BaseModel):
    capturedAt: datetime
    price: float
    isPromo: bool = False

class PriceHistoryResponse(BaseModel):
    product: Product
    history: List[PriceHistoryPoint]

# --- Mock Data ---

MOCK_STORES = [
    Store(id="1", name="Walmart", currency="MXN", region="Mexico City"),
    Store(id="2", name="Chedraui", currency="MXN", region="Mexico City"),
    Store(id="3", name="Soriana", currency="MXN", region="Mexico City"),
]

MOCK_PRODUCTS = [
    Product(id="p1", name="Leche Entera 1L", brand="Lala", sizeLabel="1L", category="Lácteos"),
    Product(id="p2", name="Huevo Blanco 30 piezas", brand="San Juan", sizeLabel="30 piezas", category="Lácteos"),
    Product(id="p3", name="Arroz Blanco 1kg", brand="Schettino", sizeLabel="1kg", category="Despensa"),
    Product(id="p4", name="Frijol Negro 1kg", brand="Verde Valle", sizeLabel="1kg", category="Despensa"),
    Product(id="p5", name="Aceite Vegetal 900ml", brand="Nutrioli", sizeLabel="900ml", category="Despensa"),
    Product(id="p6", name="Azúcar Estándar 1kg", brand="Zulka", sizeLabel="1kg", category="Despensa"),
    Product(id="p7", name="Atún en Agua 140g", brand="Dolores", sizeLabel="140g", category="Despensa"),
    Product(id="p8", name="Papel Higiénico 4 rollos", brand="Regio", sizeLabel="4 rollos", category="Limpieza"),
    Product(id="p9", name="Jabón de Trastes 750ml", brand="Axion", sizeLabel="750ml", category="Limpieza"),
    Product(id="p10", name="Detergente en Polvo 1kg", brand="Ariel", sizeLabel="1kg", category="Limpieza"),
    Product(id="p11", name="Pan de Caja 680g", brand="Bimbo", sizeLabel="680g", category="Panadería"),
    Product(id="p12", name="Mayonesa 390g", brand="McCormick", sizeLabel="390g", category="Despensa"),
    Product(id="p13", name="Pasta de Dientes 100ml", brand="Colgate", sizeLabel="100ml", category="Higiene"),
    Product(id="p14", name="Shampoo 400ml", brand="Pantene", sizeLabel="400ml", category="Higiene"),
    Product(id="p15", name="Cerveza 6 pack", brand="Corona", sizeLabel="6 pack", category="Bebidas"),
    Product(id="p16", name="Refresco de Cola 2.5L", brand="Coca-Cola", sizeLabel="2.5L", category="Bebidas"),
]

# Generate more mock products to reach 200+
categories = ["Lácteos", "Despensa", "Limpieza", "Panadería", "Higiene", "Bebidas", "Frutas y Verduras"]
for i in range(17, 210):
    cat = categories[i % len(categories)]
    MOCK_PRODUCTS.append(Product(
        id=f"p{i}", 
        name=f"Producto de Prueba {i}", 
        brand=f"Marca {cat}", 
        sizeLabel="1kg" if i % 2 == 0 else "500ml", 
        category=cat
    ))

# --- Endpoints ---

@app.get("/stores", response_model=StoreListResponse)
async def list_stores(api_key: str = Header(..., alias="X-API-Key")):
    if api_key != API_KEY:
        raise HTTPException(status_code=403, detail="Invalid API Key")
    return StoreListResponse(stores=MOCK_STORES)

@app.get("/search", response_model=ProductSearchResponse)
async def search_products(
    q: str = Query(...),
    storeId: Optional[str] = None,
    lat: Optional[float] = Query(None, description="Latitude for location-based prices"),
    lon: Optional[float] = Query(None, description="Longitude for location-based prices"),
    limit: int = Query(20, ge=1, le=100),
    cursor: Optional[str] = None,
    api_key: str = Header(..., alias="X-API-Key")
):
    if api_key != API_KEY:
        raise HTTPException(status_code=403, detail="Invalid API Key")
    
    # Basic filter by query with normalization
    search_q = q.lower().strip()
    results = []
    for p in MOCK_PRODUCTS:
        normalized_key = NormalizationService.get_normalized_search_key(p.name, p.brand)
        if search_q in normalized_key:
            results.append(p)
    
    # Cursor pagination (mock)
    start_idx = 0
    if cursor:
        try:
            start_idx = int(base64.b64decode(cursor).decode())
        except:
            pass
            
    paged_results = results[start_idx : start_idx + limit]
    next_cursor = None
    if start_idx + limit < len(results):
        next_cursor = base64.b64encode(str(start_idx + limit).encode()).decode()
        
    return ProductSearchResponse(products=paged_results, nextCursor=next_cursor)

@app.post("/list-totals", response_model=ListTotalsResponse)
async def calculate_list_totals(
    request: ListTotalsRequest = Body(...),
    api_key: str = Header(..., alias="X-API-Key")
):
    if api_key != API_KEY:
        raise HTTPException(status_code=403, detail="Invalid API Key")
    # Mock calculation: Walmart is always middle, Chedraui cheapest, Soriana expensive
    totals = []
    base_total = sum(item.quantity * 25.0 for item in request.items) # $25 avg per item
    
    totals.append(StoreTotal(storeId="1", total=base_total, updatedAt=datetime.now())) # Walmart
    totals.append(StoreTotal(storeId="2", total=base_total * 0.9, savings=base_total * 0.1, updatedAt=datetime.now())) # Chedraui
    totals.append(StoreTotal(storeId="3", total=base_total * 1.1, updatedAt=datetime.now())) # Soriana
    
    return ListTotalsResponse(totals=totals)

@app.get("/price-history", response_model=PriceHistoryResponse)
async def get_price_history(productId: str, storeId: Optional[str] = None):
    # Find product
    product = next((p for p in MOCK_PRODUCTS if p.id == productId), MOCK_PRODUCTS[0])
    
    # Generate mock history
    history = [
        PriceHistoryPoint(capturedAt=datetime(2026, 1, 1), price=24.5),
        PriceHistoryPoint(capturedAt=datetime(2026, 1, 15), price=25.0),
        PriceHistoryPoint(capturedAt=datetime(2026, 2, 1), price=24.0, isPromo=True),
    ]
    
    return PriceHistoryResponse(product=product, history=history)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
