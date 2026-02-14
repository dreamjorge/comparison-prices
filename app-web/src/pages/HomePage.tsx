import { useEffect, useState } from "react";
import { fetchStores, searchProducts, Product, Store } from "../api/client";

export function HomePage() {
  const [stores, setStores] = useState<Store[]>([]);
  const [searchQuery, setSearchQuery] = useState("leche");
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searching, setSearching] = useState(false);

  useEffect(() => {
    fetchStores()
      .then(setStores)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  const runSearch = async () => {
    const query = searchQuery.trim();
    if (!query) {
      setProducts([]);
      return;
    }

    setSearching(true);
    try {
      const results = await searchProducts(query, { includeExternalLinks: true });
      setProducts(results);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Error buscando productos");
    } finally {
      setSearching(false);
    }
  };

  useEffect(() => {
    runSearch();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const summaryCards = [
    {
      title: "Lista activa",
      body: "Arroz, leche, huevos, café y 8 ítems más.",
      badge: "Actualizado hoy"
    },
    {
      title: "Tiendas disponibles",
      body: loading
        ? "Cargando catálogo..."
        : `Comparamos precios en ${stores.length} tiendas de tu zona.`,
      badge: stores.length > 0 ? "Catálogo listo" : "Buscando tiendas"
    },
    {
      title: "Ahorro potencial",
      body: "Hasta 12% si compras en la tienda recomendada.",
      badge: "Comparador listo"
    },
    {
      title: "Alertas",
      body: "3 productos bajaron de precio esta semana.",
      badge: "Notificaciones on"
    }
  ];

  return (
    <section className="page">
      <header className="card">
        <h2>Panel de Control</h2>
        <p>
          Bienvenido al comparador web. Este dashboard está conectado al backend
          y te permite encontrar los mejores precios en tu región.
        </p>
      </header>

      {error && (
        <div className="card" style={{ border: '1px solid red' }}>
          <p style={{ color: 'red' }}>Error: {error}</p>
        </div>
      )}

      <div className="grid">
        {summaryCards.map((card) => (
          <article className="card" key={card.title}>
            <div className="badge">{card.badge}</div>
            <h3>{card.title}</h3>
            <p>{card.body}</p>
          </article>
        ))}
      </div>

      <div className="card">
        <h3>Tiendas en tu zona</h3>
        {loading ? (
          <p>Cargando tiendas...</p>
        ) : (
          <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem', flexWrap: 'wrap' }}>
            {stores.map(store => (
              <div key={store.id} style={{
                padding: '0.5rem 1rem',
                background: 'rgba(255,255,255,0.05)',
                borderRadius: '8px',
                border: '1px solid rgba(255,255,255,0.1)'
              }}>
                {store.name}
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="card">
        <h3>Búsqueda con link-out a Google Shopping</h3>
        <p className="muted">
          Los precios mostrados en esta app vienen de fuentes internas permitidas.
          Google Shopping se usa solo como navegación externa de validación.
        </p>
        <div style={{ display: "flex", gap: "0.75rem", flexWrap: "wrap", marginTop: "1rem" }}>
          <input
            value={searchQuery}
            onChange={(event) => setSearchQuery(event.target.value)}
            placeholder="Ejemplo: leche entera 1L"
            style={{ flex: "1 1 280px", padding: "0.6rem 0.75rem", borderRadius: "8px", border: "1px solid #d2d6e0" }}
          />
          <button className="btn-primary" onClick={runSearch} disabled={searching}>
            {searching ? "Buscando..." : "Buscar"}
          </button>
        </div>

        <div style={{ display: "grid", gap: "0.75rem", marginTop: "1rem" }}>
          {products.map((product) => (
            <article key={product.id} className="product-item">
              <div>
                <strong>{product.name}</strong>
                <p className="muted small">
                  Fuente: {(product.sourceHints ?? []).join(", ") || "provider_blend"}
                </p>
              </div>
              {product.externalUrl ? (
                <a className="btn-secondary" href={product.externalUrl} target="_blank" rel="noreferrer">
                  Ver en Google Shopping
                </a>
              ) : (
                <span className="small muted">Sin link externo</span>
              )}
            </article>
          ))}
        </div>
      </div>
    </section>
  );
}
