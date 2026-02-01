const summaryCards = [
  {
    title: "Lista activa",
    body: "Arroz, leche, huevos, café y 8 ítems más.",
    badge: "Actualizado hoy"
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

export function HomePage() {
  return (
    <section className="page">
      <div className="card">
        <h2>Resumen rápido</h2>
        <p>
          Este dashboard prepara el terreno para el flujo web: lista activa,
          comparación por tienda y alertas.
        </p>
      </div>
      <div className="grid">
        {summaryCards.map((card) => (
          <article className="card" key={card.title}>
            <div className="badge">{card.badge}</div>
            <h3>{card.title}</h3>
            <p>{card.body}</p>
          </article>
        ))}
      </div>
    </section>
  );
}
