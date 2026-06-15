import { randomUUID } from "node:crypto";
import { and, desc, eq, lt } from "drizzle-orm";
import { db } from "../../config/db.ts";
import { equipes, execucoesRota } from "../../db/schema.ts";
import { redisGet, redisSetEx } from "../../config/redis.ts";
import { getOrSet, evictByPrefix } from "../../cache/cache.ts";
import { EntityNotFoundError, RouteProcessingError } from "../../errors.ts";
import type { RouteRequest } from "../../schemas/index.ts";
import { callOrs, toOrsRequest } from "./ors.ts";
import type { OrsOptimizationResponse } from "./ors.ts";

const HISTORICO_PREFIX = "historico_rotas:";
const ROUTE_CACHE_TTL_SECONDS = 24 * 60 * 60; // 24h

export interface RouteHistory {
  id: string;
  teamId: number | null;
  date: string | null;
  createdAt: string | null;
}

/** Data local no formato YYYY-MM-DD (equivale a LocalDate.now()). */
function today(): string {
  const d = new Date();
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${y}-${m}-${day}`;
}

/** Hash determinístico da lista de clientes para compor a chave de cache da rota. */
function clientsHash(clients: RouteRequest["clients"]): string {
  const repr = clients.map((c) => `${c.instalacao}:${c.latitude}:${c.longitude}`).join("|");
  return Bun.hash(repr).toString();
}

export async function calculateRoute(req: RouteRequest): Promise<OrsOptimizationResponse> {
  let startLat = req.currentLat ?? null;
  let startLon = req.currentLon ?? null;

  if (startLat === null || startLon === null) {
    const team = await db
      .select({ lat: equipes.latitudeBase, lon: equipes.longitudeBase })
      .from(equipes)
      .where(eq(equipes.id, req.teamId))
      .limit(1);
    if (team.length === 0) {
      throw new EntityNotFoundError(`Equipe não encontrada com ID: ${req.teamId}`);
    }
    startLat = team[0]!.lat;
    startLon = team[0]!.lon;
  }

  if (startLat === null || startLon === null) {
    throw new RouteProcessingError("Coordenadas de início indisponíveis para a equipe.");
  }

  const date = today();
  const startPointKey = `${startLat.toFixed(4)},${startLon.toFixed(4)}`;
  const cacheKey = `rota:equipe:${req.teamId}:data:${date}:start:${startPointKey}:clients:${clientsHash(req.clients)}`;

  // Cache manual de 24h (replica o StringRedisTemplate do Java).
  const cached = await redisGet(cacheKey).catch(() => null);
  if (cached) {
    const trimmed = cached.trim();
    if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) {
      throw new RouteProcessingError("Cache inválido: Conteúdo não é um JSON válido.");
    }
    return JSON.parse(trimmed) as OrsOptimizationResponse;
  }

  const orsRequest = toOrsRequest(req.clients, startLat, startLon);
  const response = await callOrs(orsRequest);

  const json = JSON.stringify(response);
  await redisSetEx(cacheKey, json, ROUTE_CACHE_TTL_SECONDS).catch((err) =>
    console.error("[routes] falha ao gravar cache da rota:", err),
  );

  // Upsert por (equipe_id, data): mantém o id se já houver execução no dia.
  const existing = await db
    .select({ id: execucoesRota.id })
    .from(execucoesRota)
    .where(and(eq(execucoesRota.equipeId, req.teamId), eq(execucoesRota.data, date)))
    .limit(1);

  if (existing.length > 0) {
    await db.update(execucoesRota).set({ rotaJson: json }).where(eq(execucoesRota.id, existing[0]!.id));
  } else {
    await db.insert(execucoesRota).values({
      id: randomUUID(),
      equipeId: req.teamId,
      data: date,
      rotaJson: json,
    });
  }

  // Invalida o histórico cacheado da equipe (equivale ao @CacheEvict).
  await evictByPrefix(`${HISTORICO_PREFIX}${req.teamId}`);

  return response;
}

export function getRouteHistory(teamId: number): Promise<RouteHistory[]> {
  return getOrSet(`${HISTORICO_PREFIX}${teamId}`, async () => {
    const team = await db.select({ id: equipes.id }).from(equipes).where(eq(equipes.id, teamId)).limit(1);
    if (team.length === 0) {
      throw new EntityNotFoundError(`Equipe não encontrada com ID: ${teamId}`);
    }

    // Seleciona apenas id/equipe_id/data — NÃO carrega rota_json (TEXT grande): economia de RAM.
    const rows = await db
      .select({ id: execucoesRota.id, equipeId: execucoesRota.equipeId, data: execucoesRota.data })
      .from(execucoesRota)
      .where(eq(execucoesRota.equipeId, teamId))
      .orderBy(desc(execucoesRota.data));

    return rows.map((r) => ({
      id: r.id,
      teamId: r.equipeId,
      date: r.data,
      createdAt: r.data, // RouteHistoryMapper: createdAt = data.toString()
    }));
  });
}

export async function getRouteDetails(id: string): Promise<OrsOptimizationResponse> {
  const rows = await db
    .select({ rotaJson: execucoesRota.rotaJson })
    .from(execucoesRota)
    .where(eq(execucoesRota.id, id))
    .limit(1);
  if (rows.length === 0 || rows[0]!.rotaJson === null) {
    throw new EntityNotFoundError(`Rota não encontrada com ID: ${id}`);
  }
  try {
    return JSON.parse(rows[0]!.rotaJson!) as OrsOptimizationResponse;
  } catch {
    throw new RouteProcessingError("Erro ao processar dados da rota.");
  }
}

/** Remove rotas com mais de 2 dias e limpa o cache de histórico (cron diário). */
export async function cleanupOldRoutes(): Promise<void> {
  const cutoff = new Date();
  cutoff.setDate(cutoff.getDate() - 2);
  const y = cutoff.getFullYear();
  const m = String(cutoff.getMonth() + 1).padStart(2, "0");
  const day = String(cutoff.getDate()).padStart(2, "0");
  const cutoffStr = `${y}-${m}-${day}`;

  await db.delete(execucoesRota).where(lt(execucoesRota.data, cutoffStr));
  await evictByPrefix(HISTORICO_PREFIX);
}
