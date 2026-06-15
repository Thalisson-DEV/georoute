import { getOrSet, evictByPrefix } from "../../cache/cache.ts";
import { EntityAlreadyExistsError, EntityNotFoundError } from "../../errors.ts";
import type { ClientesRequest } from "../../schemas/index.ts";
import * as repo from "./clientes.repo.ts";
import type { PageResult, SortSpec } from "./clientes.repo.ts";

const CACHE_PREFIX = "clientes_";

function sortKey(specs: SortSpec[]): string {
  return specs.length === 0 ? "unsorted" : specs.map((s) => `${String(s.field)},${s.dir}`).join(";");
}

export async function saveCliente(req: ClientesRequest): Promise<void> {
  if (await repo.existsByInstalacao(req.instalacao)) {
    throw new EntityAlreadyExistsError(`Cliente já existente com o id: ${req.instalacao}`);
  }
  await repo.insertCliente({
    instalacao: req.instalacao,
    contaContrato: req.contaContrato ?? null,
    numeroSerie: req.numeroSerie ?? null,
    numeroPoste: req.numeroPoste ?? null,
    nomeCliente: req.nomeCliente ?? null,
    latitude: req.latitude ?? null,
    longitude: req.longitude ?? null,
  });
}

export function findByInstalacao(instalacao: number) {
  return getOrSet(`${CACHE_PREFIX}instalacao:${instalacao}`, async () => {
    const cliente = await repo.findByInstalacao(instalacao);
    if (!cliente) {
      throw new EntityNotFoundError(`Cliente inexistente com o id: ${instalacao}`);
    }
    return cliente;
  });
}

async function findPaginatedCached(
  cacheName: string,
  identity: string | number,
  page: number,
  size: number,
  sortSpecs: SortSpec[],
  loader: () => Promise<PageResult>,
): Promise<PageResult> {
  const key = `${CACHE_PREFIX}${cacheName}:${identity},${page},${size},${sortKey(sortSpecs)}`;
  return getOrSet(key, async () => {
    const result = await loader();
    if (result.data.length === 0) {
      throw new EntityNotFoundError(`Cliente inexistente com o id: ${identity}`);
    }
    return result;
  });
}

export function findByContaContrato(conta: number, page: number, size: number, sortSpecs: SortSpec[]) {
  return findPaginatedCached("conta_contrato", conta, page, size, sortSpecs, () =>
    repo.findByContaContrato(conta, page, size, sortSpecs),
  );
}

export function findByNumeroSerie(numeroSerie: number, page: number, size: number, sortSpecs: SortSpec[]) {
  return findPaginatedCached("numero_serie", numeroSerie, page, size, sortSpecs, () =>
    repo.findByNumeroSerie(numeroSerie, page, size, sortSpecs),
  );
}

export function findByNumeroPoste(numeroPoste: string, page: number, size: number, sortSpecs: SortSpec[]) {
  return findPaginatedCached("numero_poste", numeroPoste, page, size, sortSpecs, () =>
    repo.findByNumeroPoste(numeroPoste, page, size, sortSpecs),
  );
}

/** Invalida todos os caches de clientes (usado após a importação CSV). */
export function evictAllClientesCache(): Promise<void> {
  return evictByPrefix(CACHE_PREFIX);
}
