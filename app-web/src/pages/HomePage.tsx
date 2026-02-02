import { useEffect, useState } from "react";
import { fetchStores, Store } from "../api/client";

export function HomePage() {
  const [stores, setStores] = useState<Store[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchStores()
      .then(setStores)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
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
          Bienvenido al comparador web. Aquí puedes ver el estado de tu lista
          y las tiendas sincronizadas.
        </p>
      </header>

      {error && (
        <div className="card" style={{ borderColor: 'red' }}>
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
          <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem' }}>
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
    </section>
  );
}
