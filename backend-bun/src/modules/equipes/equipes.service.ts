import { and, eq } from "drizzle-orm";
import { db } from "../../config/db.ts";
import { equipes } from "../../db/schema.ts";
import { getOrSet, evictByPrefix } from "../../cache/cache.ts";
import { EntityAlreadyExistsError, EntityNotFoundError } from "../../errors.ts";
import type { EquipesRequest, Municipio, Setor } from "../../schemas/index.ts";

const CACHE_PREFIX = "equipes:";

export interface EquipeResponse {
  id: number;
  nome: string | null;
  latitudeBase: number | null;
  longitudeBase: number | null;
  setor: string | null;
  municipio: string;
}

const responseColumns = {
  id: equipes.id,
  nome: equipes.nome,
  latitudeBase: equipes.latitudeBase,
  longitudeBase: equipes.longitudeBase,
  setor: equipes.setor,
  municipio: equipes.municipio,
};

async function existsByNome(nome: string): Promise<boolean> {
  const rows = await db.select({ id: equipes.id }).from(equipes).where(eq(equipes.nome, nome)).limit(1);
  return rows.length > 0;
}

function evictCache(): Promise<void> {
  return evictByPrefix(CACHE_PREFIX);
}

export async function saveEquipe(req: EquipesRequest): Promise<void> {
  if (await existsByNome(req.nome)) {
    throw new EntityAlreadyExistsError(`Já existe uma equipe cadastrada com o nome: ${req.nome}`);
  }
  await db.insert(equipes).values({
    nome: req.nome,
    latitudeBase: req.latitudeBase,
    longitudeBase: req.longitudeBase,
    setor: req.setor,
    municipio: req.municipio,
  });
  await evictCache();
}

export async function updateEquipe(id: number, req: EquipesRequest): Promise<void> {
  const existing = await db.select({ nome: equipes.nome }).from(equipes).where(eq(equipes.id, id)).limit(1);
  if (existing.length === 0) {
    throw new EntityNotFoundError(`Equipe não encontrada com ID: ${id}`);
  }
  if (existing[0]!.nome !== req.nome && (await existsByNome(req.nome))) {
    throw new EntityAlreadyExistsError(`Já existe uma equipe com o nome: ${req.nome}`);
  }
  await db
    .update(equipes)
    .set({
      nome: req.nome,
      latitudeBase: req.latitudeBase,
      longitudeBase: req.longitudeBase,
      setor: req.setor,
      municipio: req.municipio,
    })
    .where(eq(equipes.id, id));
  await evictCache();
}

export async function deleteEquipe(id: number): Promise<void> {
  const existing = await db.select({ id: equipes.id }).from(equipes).where(eq(equipes.id, id)).limit(1);
  if (existing.length === 0) {
    throw new EntityNotFoundError(`Equipe não encontrada com ID: ${id}`);
  }
  await db.delete(equipes).where(eq(equipes.id, id));
  await evictCache();
}

export function findAllEquipes(setor?: Setor, municipio?: Municipio): Promise<EquipeResponse[]> {
  const key = `${CACHE_PREFIX}${setor ?? "null"}-${municipio ?? "null"}`;
  return getOrSet(key, async () => {
    const conditions = [];
    if (setor) conditions.push(eq(equipes.setor, setor));
    if (municipio) conditions.push(eq(equipes.municipio, municipio));

    let query = db.select(responseColumns).from(equipes).$dynamic();
    if (conditions.length > 0) {
      query = query.where(conditions.length === 1 ? conditions[0]! : and(...conditions));
    }
    return (await query) as EquipeResponse[];
  });
}
