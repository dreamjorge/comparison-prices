import { useEffect, useState } from "react";
import { fetchStores, Store } from "../services/api";

export function HomePage() {
  const [stores, setStores] = useState<Store[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStores()
      .then((data) => setStores(data.stores))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const summaryCards = [
    {
      title: "Tiendas disponibles",
      body: loading ? "Cargando..." : `${stores.length} tiendas configuradas en tu zona.`,
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

  return (
    <section className="page">
      <div className="card">
        <h2>Resumen rápido</h2>
        <p>
          Este dashboard ahora está conectado al backend FastAPI.
          Encuentra los mejores precios en las tiendas de tu región.
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
