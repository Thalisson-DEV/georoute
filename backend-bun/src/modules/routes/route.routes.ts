import { Hono } from "hono";
import { AppError } from "../../errors.ts";
import { routeRequestSchema, validate } from "../../schemas/index.ts";
import * as service from "./route.service.ts";

export const routeRoutes = new Hono();

// POST /optimize — calcula a rota otimizada (200).
routeRoutes.post("/optimize", async (c) => {
  const dto = validate(routeRequestSchema, await c.req.json());
  return c.json(await service.calculateRoute(dto));
});

// GET /history/:teamId — histórico de rotas da equipe (200).
routeRoutes.get("/history/:teamId", async (c) => {
  const teamId = Number(c.req.param("teamId"));
  if (!Number.isFinite(teamId)) throw new AppError("ID de equipe inválido", 400);
  return c.json(await service.getRouteHistory(teamId));
});

// GET /:id — detalhes (JSON completo) de uma rota executada (200).
routeRoutes.get("/:id", async (c) => {
  const id = c.req.param("id");
  return c.json(await service.getRouteDetails(id));
});
