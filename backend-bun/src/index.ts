import { Hono } from "hono";
import { env } from "./config/env.ts";
import { corsMiddleware } from "./middleware/cors.ts";
import { errorHandler } from "./middleware/error-handler.ts";
import { clientesRoutes } from "./modules/clientes/clientes.routes.ts";
import { equipesRoutes } from "./modules/equipes/equipes.routes.ts";
import { routeRoutes } from "./modules/routes/route.routes.ts";
import { mapsRoutes } from "./modules/maps/maps.routes.ts";
import { startCleanupScheduler } from "./cron/cleanup.ts";

const app = new Hono();

app.use("*", corsMiddleware);
app.onError(errorHandler);

// Health check simples (substitui o /actuator/health).
app.get("/health", (c) => c.json({ status: "UP" }));

// Rotas da API — mesmos contratos do back-end Java (base /api/v1).
app.route("/api/v1/clientes", clientesRoutes);
app.route("/api/v1/equipes", equipesRoutes);
app.route("/api/v1/routes", routeRoutes);
app.route("/api/v1/maps", mapsRoutes);

// Agendador de limpeza diária de rotas.
startCleanupScheduler();

console.log(`GeoRoute backend (Bun) ouvindo na porta ${env.port}`);

export default {
  port: env.port,
  fetch: app.fetch,
};
