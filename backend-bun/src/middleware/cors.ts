import { cors } from "hono/cors";
import { env } from "../config/env.ts";

// Replica o CorsConfig do Java:
// origens: FRONTEND_URL, http://localhost:4200, https://*.vercel.app
// métodos: GET, POST, PUT, DELETE, OPTIONS, PATCH
// allowCredentials: true; expõe header Authorization
const staticOrigins = new Set<string>([env.frontendUrl, "http://localhost:4200"]);

function resolveOrigin(origin: string): string | null {
  if (!origin) return null;
  if (staticOrigins.has(origin)) return origin;
  // https://*.vercel.app
  try {
    const url = new URL(origin);
    if (url.protocol === "https:" && url.hostname.endsWith(".vercel.app")) {
      return origin;
    }
  } catch {
    return null;
  }
  return null;
}

export const corsMiddleware = cors({
  origin: (origin) => resolveOrigin(origin),
  allowMethods: ["GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"],
  allowHeaders: ["*"],
  exposeHeaders: ["Authorization"],
  credentials: true,
});
