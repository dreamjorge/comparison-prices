const storeTotals = [
  { store: "Super A", total: "$18.430", savings: "-8%" },
  { store: "Mercado Express", total: "$19.980", savings: "-2%" },
  { store: "Hiper Descuento", total: "$20.400", savings: "+0%" }
];

export function ComparePage() {
  return (
    <section className="page">
      <div className="card">
        <h2>Comparador de tiendas</h2>
        <p>
          Esta vista presentará el total por tienda usando los contratos
          compartidos del backend.
        </p>
      </div>
      <div className="grid">
        {storeTotals.map((store, index) => (
          <article className="card" key={store.store}>
            <div className="badge">
              {index === 0 ? "Mejor opción" : "Alternativa"}
            </div>
            <h3>{store.store}</h3>
            <p>Total estimado: {store.total}</p>
            <p>Ahorro vs promedio: {store.savings}</p>
          </article>
        ))}
      </div>
    </section>
  );
}
