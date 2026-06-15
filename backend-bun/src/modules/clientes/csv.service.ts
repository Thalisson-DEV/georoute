import { createReadStream } from "node:fs";
import { unlink } from "node:fs/promises";
import { parse } from "csv-parse";
import { pg, upsertClientesBatch } from "./clientes.repo.ts";
import type { ClienteUpsertRow } from "./clientes.repo.ts";
import { evictAllClientesCache } from "./clientes.service.ts";

const BATCH_SIZE = 1000;

function parseLong(value: string | undefined): number | null {
  if (value === undefined) return null;
  const trimmed = value.trim();
  if (trimmed === "") return null;
  const n = Number(trimmed);
  return Number.isFinite(n) ? Math.trunc(n) : null;
}

function parseDouble(value: string | undefined): number | null {
  if (value === undefined) return null;
  const trimmed = value.trim();
  if (trimmed === "") return null;
  const n = Number(trimmed);
  return Number.isFinite(n) ? n : null;
}

function parseString(value: string | undefined): string | null {
  if (value === undefined) return null;
  const trimmed = value.trim();
  return trimmed === "" ? null : trimmed;
}

function toRow(record: Record<string, string>): ClienteUpsertRow {
  const instalacao = parseLong(record["instalacao"]);
  if (instalacao === null) {
    // Equivale à validação @NotNull do Java (aborta a importação).
    throw new Error(
      `Erro de validação no registro (Instalação: ${record["instalacao"]}): O numero da instalação não pode estar vazio.`,
    );
  }
  return {
    instalacao,
    conta_contrato: parseLong(record["conta_contrato"]),
    numero_serie: parseLong(record["numero_serie"]),
    numero_poste: parseString(record["numero_poste"]),
    nome_cliente: parseString(record["nome_cliente"]),
    latitude: parseDouble(record["latitude"]),
    longitude: parseDouble(record["longitude"]),
  };
}

/**
 * Processa o CSV em STREAMING, fazendo upsert em lotes de 1000 dentro de uma
 * única transação (all-or-nothing, como o back-end Java, porém sem carregar o
 * arquivo inteiro na memória). Ao final, invalida o cache de clientes e remove
 * o arquivo temporário. Executado em segundo plano (fire-and-forget).
 */
export async function processCsvFile(filePath: string): Promise<void> {
  console.log(`Iniciando processamento do arquivo CSV: ${filePath}`);
  try {
    let total = 0;
    await pg.begin(async (tx) => {
      const parser = createReadStream(filePath).pipe(
        parse({
          columns: true,
          bom: true,
          skip_empty_lines: true,
          relax_column_count: true,
          trim: true,
        }),
      );

      let batch: ClienteUpsertRow[] = [];
      for await (const record of parser) {
        batch.push(toRow(record as Record<string, string>));
        if (batch.length >= BATCH_SIZE) {
          await upsertClientesBatch(tx as unknown as typeof pg, batch);
          total += batch.length;
          batch = [];
        }
      }
      if (batch.length > 0) {
        await upsertClientesBatch(tx as unknown as typeof pg, batch);
        total += batch.length;
      }
    });

    console.log(`CSV processado com sucesso. ${total} registros (upsert).`);
    await evictAllClientesCache();
    console.log("Cache de clientes invalidado.");
  } catch (err) {
    console.error("Erro fatal no processamento do CSV:", err);
  } finally {
    try {
      await unlink(filePath);
      console.log("Arquivo temporário removido.");
    } catch {
      console.warn(`Falha ao remover arquivo temporário: ${filePath}`);
    }
  }
}
