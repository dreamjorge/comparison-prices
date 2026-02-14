import { useEffect, useState } from "react";
import { calculateListTotals, fetchStores, ListTotalsApiResponse, ListItem, Store, StoreTotal } from "../api/client";

const DEMO_ITEMS: ListItem[] = [
  { productId: "p1", quantity: 2 }, // Leche
  { productId: "p2", quantity: 1 }, // Pan
  { productId: "p3", quantity: 1 }  // Arroz
];

export function ComparePage() {
  const [totals, setTotals] = useState<StoreTotal[]>([]);
  const [coverage, setCoverage] = useState<ListTotalsApiResponse["coverage"] | null>(null);
  const [warnings, setWarnings] = useState<string[]>([]);
  const [stores, setStores] = useState<Store[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    Promise.all([
      calculateListTotals(DEMO_ITEMS),
      fetchStores()
    ])
      .then(([totalsData, storesData]) => {
        setTotals(totalsData.totals);
        setCoverage(totalsData.coverage);
        setWarnings(totalsData.warnings ?? []);
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
        <p className="muted">
          Fuente interna: provider_blend. Precios pueden variar; verifica en tienda/fuente.
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
        <>
          <div className="card">
            <h3>Cobertura de lista</h3>
            <p>
              Items con precio encontrado: {coverage?.matchedItems ?? 0}. Sin cobertura: {coverage?.unmatchedItems ?? 0}.
            </p>
            {warnings.length > 0 && (
              <ul>
                {warnings.map((warning) => (
                  <li key={warning}>{warning}</li>
                ))}
              </ul>
            )}
          </div>
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
                  {typeof t.matchedItems === "number" && (
                    <p className="muted small">
                      Cobertura tienda: {t.matchedItems}/{DEMO_ITEMS.length} productos
                    </p>
                  )}
                  {t.savings !== null && (
                    <p style={{ color: '#4caf50', fontWeight: 'bold' }}>
                      ¡Ahorras ${t.savings.toFixed(2)} vs siguiente opción!
                    </p>
                  )}
                  <p style={{ fontSize: '0.8rem', opacity: 0.6, marginTop: '0.5rem' }}>
                    Fuente: {t.source ?? "provider_blend"} | Actualizado: {new Date(t.updatedAt || "").toLocaleTimeString()}
                  </p>
                </article>
              );
            })}
          </div>
        </>
      )}
    </section>
  );
}
