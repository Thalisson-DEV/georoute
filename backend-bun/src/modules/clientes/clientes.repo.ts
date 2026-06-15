import { asc, desc, eq, sql as dsql } from "drizzle-orm";
import type { SQL } from "drizzle-orm";
import type { AnyPgColumn } from "drizzle-orm/pg-core";
import type { Sql } from "postgres";
import { db, sql } from "../../config/db.ts";
import { clientes } from "../../db/schema.ts";

export interface ClienteResponse {
  instalacao: number;
  nomeCliente: string | null;
  latitude: number | null;
  longitude: number | null;
}

export interface PageResult {
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  data: ClienteResponse[];
}

export interface SortSpec {
  field: keyof typeof clientes.$inferSelect;
  dir: "asc" | "desc";
}

// Seleção enxuta: apenas as colunas do ClienteResponse (economia de memória/transferência).
const responseColumns = {
  instalacao: clientes.instalacao,
  nomeCliente: clientes.nomeCliente,
  latitude: clientes.latitude,
  longitude: clientes.longitude,
};

export async function existsByInstalacao(instalacao: number): Promise<boolean> {
  const rows = await db
    .select({ one: dsql<number>`1` })
    .from(clientes)
    .where(eq(clientes.instalacao, instalacao))
    .limit(1);
  return rows.length > 0;
}

export async function findByInstalacao(instalacao: number): Promise<ClienteResponse | null> {
  const rows = await db
    .select(responseColumns)
    .from(clientes)
    .where(eq(clientes.instalacao, instalacao))
    .limit(1);
  return rows[0] ?? null;
}

export async function insertCliente(row: typeof clientes.$inferInsert): Promise<void> {
  await db.insert(clientes).values(row);
}

const sortColumnMap: Record<string, AnyPgColumn> = {
  instalacao: clientes.instalacao,
  contaContrato: clientes.contaContrato,
  numeroSerie: clientes.numeroSerie,
  numeroPoste: clientes.numeroPoste,
  nomeCliente: clientes.nomeCliente,
  latitude: clientes.latitude,
  longitude: clientes.longitude,
};

function buildOrderBy(sortSpecs: SortSpec[]): SQL[] {
  const order: SQL[] = [];
  for (const spec of sortSpecs) {
    const col = sortColumnMap[spec.field as string];
    if (col) order.push(spec.dir === "desc" ? desc(col) : asc(col));
  }
  return order;
}

async function findPageByColumn(
  column: typeof clientes.contaContrato | typeof clientes.numeroSerie | typeof clientes.numeroPoste,
  value: number | string,
  page: number,
  size: number,
  sortSpecs: SortSpec[],
): Promise<PageResult> {
  const whereClause = eq(column, value as never);

  const countRows = await db
    .select({ count: dsql<number>`count(*)::int` })
    .from(clientes)
    .where(whereClause);
  const totalElements = countRows[0]?.count ?? 0;

  const order = buildOrderBy(sortSpecs);
  let query = db.select(responseColumns).from(clientes).where(whereClause).$dynamic();
  if (order.length > 0) query = query.orderBy(...order);
  const data = await query.limit(size).offset(page * size);

  return {
    pageNumber: page,
    pageSize: size,
    totalElements,
    totalPages: size > 0 ? Math.ceil(totalElements / size) : 0,
    data,
  };
}

export function findByContaContrato(conta: number, page: number, size: number, sortSpecs: SortSpec[]) {
  return findPageByColumn(clientes.contaContrato, conta, page, size, sortSpecs);
}

export function findByNumeroSerie(numeroSerie: number, page: number, size: number, sortSpecs: SortSpec[]) {
  return findPageByColumn(clientes.numeroSerie, numeroSerie, page, size, sortSpecs);
}

export function findByNumeroPoste(numeroPoste: string, page: number, size: number, sortSpecs: SortSpec[]) {
  return findPageByColumn(clientes.numeroPoste, numeroPoste, page, size, sortSpecs);
}

export interface ClienteUpsertRow {
  instalacao: number;
  conta_contrato: number | null;
  numero_serie: number | null;
  numero_poste: string | null;
  nome_cliente: string | null;
  latitude: number | null;
  longitude: number | null;
}

/**
 * Upsert em lote replicando exatamente o SQL do back-end Java:
 * ON CONFLICT (instalacao) DO UPDATE ... WHERE col IS DISTINCT FROM EXCLUDED.col ...
 * Recebe a instância `sql` (pode ser uma transação) para controle transacional.
 */
export async function upsertClientesBatch(sqlInstance: Sql, rows: ClienteUpsertRow[]): Promise<void> {
  if (rows.length === 0) return;
  await sqlInstance`
    INSERT INTO clientes ${sqlInstance(
      rows,
      "instalacao",
      "conta_contrato",
      "numero_serie",
      "numero_poste",
      "nome_cliente",
      "latitude",
      "longitude",
    )}
    ON CONFLICT (instalacao) DO UPDATE SET
      conta_contrato = EXCLUDED.conta_contrato,
      numero_serie = EXCLUDED.numero_serie,
      numero_poste = EXCLUDED.numero_poste,
      nome_cliente = EXCLUDED.nome_cliente,
      latitude = EXCLUDED.latitude,
      longitude = EXCLUDED.longitude
    WHERE
      clientes.conta_contrato IS DISTINCT FROM EXCLUDED.conta_contrato OR
      clientes.numero_serie IS DISTINCT FROM EXCLUDED.numero_serie OR
      clientes.numero_poste IS DISTINCT FROM EXCLUDED.numero_poste OR
      clientes.nome_cliente IS DISTINCT FROM EXCLUDED.nome_cliente OR
      clientes.latitude IS DISTINCT FROM EXCLUDED.latitude OR
      clientes.longitude IS DISTINCT FROM EXCLUDED.longitude
  `;
}

// Reexporta a instância sql para uso transacional pelo serviço de CSV.
export { sql as pg };
