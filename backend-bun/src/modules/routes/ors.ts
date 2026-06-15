import { env } from "../../config/env.ts";
import { UpstreamError } from "../../errors.ts";

interface ClientPoint {
  instalacao: number;
  latitude: number;
  longitude: number;
}

interface OrsJob {
  id: number;
  service: number;
  location: [number, number];
}

interface OrsVehicle {
  id: number;
  profile: string;
  start: [number, number];
  end: [number, number];
}

interface OrsRequest {
  jobs: OrsJob[];
  vehicles: OrsVehicle[];
}

// Projeção idêntica aos DTOs Java (campos extras do ORS são descartados).
export interface OrsStep {
  type: string;
  location: number[];
  id: number | null;
  arrival: number | null;
  duration: number | null;
}
export interface OrsRoute {
  vehicle: number | null;
  steps: OrsStep[];
}
export interface OrsSummary {
  cost: number | null;
  duration: number | null;
  distance: number | null;
}
export interface OrsOptimizationResponse {
  routes: OrsRoute[];
  summary: OrsSummary | null;
}

/** Monta a requisição ORS (replica RouteMapper): location = [lon, lat], service = 300. */
export function toOrsRequest(clients: ClientPoint[], startLat: number, startLon: number): OrsRequest {
  const jobs: OrsJob[] = clients.map((c) => ({
    id: c.instalacao,
    service: 300,
    location: [c.longitude, c.latitude],
  }));
  const startLocation: [number, number] = [startLon, startLat];
  const vehicle: OrsVehicle = {
    id: 1,
    profile: "driving-car",
    start: startLocation,
    end: startLocation,
  };
  return { jobs, vehicles: [vehicle] };
}

/** Projeta a resposta crua do ORS no formato exato esperado pelo front-end. */
function project(raw: any): OrsOptimizationResponse {
  const routes: OrsRoute[] = Array.isArray(raw?.routes)
    ? raw.routes.map((r: any) => ({
        vehicle: r?.vehicle ?? null,
        steps: Array.isArray(r?.steps)
          ? r.steps.map((s: any) => ({
              type: s?.type,
              location: s?.location,
              id: s?.id ?? null,
              arrival: s?.arrival ?? null,
              duration: s?.duration ?? null,
            }))
          : [],
      }))
    : [];
  const summary: OrsSummary | null = raw?.summary
    ? {
        cost: raw.summary.cost ?? null,
        duration: raw.summary.duration ?? null,
        distance: raw.summary.distance ?? null,
      }
    : null;
  return { routes, summary };
}

/** Chama o OpenRouteService. Repassa o status HTTP em caso de erro (como WebClientResponseException). */
export async function callOrs(request: OrsRequest): Promise<OrsOptimizationResponse> {
  const res = await fetch(env.ors.url, {
    method: "POST",
    headers: {
      Authorization: env.ors.apiKey,
      "Content-Type": "application/json",
    },
    body: JSON.stringify(request),
  });

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new UpstreamError(`Erro no OpenRouteService: ${text || res.statusText}`, res.status);
  }

  const raw = await res.json();
  return project(raw);
}
