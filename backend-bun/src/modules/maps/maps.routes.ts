import { Hono } from "hono";
import { AppError } from "../../errors.ts";

export const mapsRoutes = new Hono();

// GET /redirect — redireciona (302) para o Google Maps com base nas coordenadas.
mapsRoutes.get("/redirect", (c) => {
  const latitude = c.req.query("latitude");
  const longitude = c.req.query("longitude");

  if (latitude === undefined || longitude === undefined) {
    throw new AppError("Coordenadas inválidas", 400);
  }

  const url = `https://www.google.com/maps?q=${latitude},${longitude}`;
  return c.redirect(url, 302);
});
