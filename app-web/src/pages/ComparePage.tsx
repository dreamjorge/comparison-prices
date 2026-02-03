import { useEffect, useState } from "react";
import { calculateListTotals, fetchStores, Store, StoreTotal, ListItem } from "../api/client";

const DEMO_ITEMS: ListItem[] = [
  { productId: "p1", quantity: 2 }, // Leche
  { productId: "p2", quantity: 1 }, // Pan
  { productId: "p3", quantity: 1 }  // Arroz
];

export function ComparePage() {
  const [totals, setTotals] = useState<StoreTotal[]>([]);
  const [stores, setStores] = useState<Store[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    Promise.all([
      calculateListTotals(DEMO_ITEMS),
      fetchStores()
    ])
      .then(([totalsData, storesData]) => {
        setTotals(totalsData);
        setStores(storesData);
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  const getStoreName = (id: string) => stores.find(s => s.id === id)?.name || "Tienda desconocida";

  return (
    <section className="page">
      <header className="card">
        <h2>Comparador de tiendas</h2>
        <p>
          Calculando el total de tu lista "Compra semanal" (3 productos)
          en tiempo real desde el servidor.
        </p>
      </header>

      {error && (
        <div className="card" style={{ border: '1px solid red' }}>
          <p style={{ color: 'red' }}>Error: {error}</p>
        </div>
      )}

      {loading ? (
        <div className="card">
          <p>Calculando mejores precios...</p>
        </div>
      ) : (
        <div className="grid">
          {totals.map((t) => {
            const isCheapest = t.savings !== null;
            return (
              <article className="card" key={t.storeId} style={{
                border: isCheapest ? '1px solid #4caf50' : '1px solid rgba(255,255,255,0.2)',
                background: isCheapest ? 'rgba(76, 175, 80, 0.05)' : 'transparent'
              }}>
                <div className="badge">
                  {isCheapest ? "Mejor opción" : "Alternativa"}
                </div>
                <h3>{getStoreName(t.storeId)}</h3>
                <p style={{ fontSize: '1.5rem', fontWeight: 'bold' }}>
                  Total: ${t.total.toFixed(2)}
                </p>
                {t.savings !== null && (
                  <p style={{ color: '#4caf50', fontWeight: 'bold' }}>
                    ¡Ahorras ${t.savings.toFixed(2)} vs siguiente opción!
                  </p>
                )}
                <p style={{ fontSize: '0.8rem', opacity: 0.6, marginTop: '0.5rem' }}>
                  Actualizado: {new Date(t.updatedAt || "").toLocaleTimeString()}
                </p>
              </article>
            );
          })}
        </div>
      )}
    </section>
  );
}
