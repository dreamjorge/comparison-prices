import { NavLink, Outlet } from "react-router-dom";

const navItems = [
  { label: "Resumen", to: "/" },
  { label: "Comparador", to: "/comparador" }
];

export function AppLayout() {
  return (
    <div className="app-shell">
      <header className="app-header">
        <h1>Comparador de precios</h1>
        <p>Monitorea tu lista y encuentra la mejor tienda en segundos.</p>
        <nav className="app-nav">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) => (isActive ? "active" : undefined)}
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
      </header>
      <main className="app-main">
        <Outlet />
      </main>
      <footer className="app-footer">
        Datos demo · Web MVP conectado a contratos compartidos
      </footer>
    </div>
  );
}
