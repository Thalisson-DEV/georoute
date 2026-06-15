import { Hono } from "hono";
import { tmpdir } from "node:os";
import { join } from "node:path";
import { randomUUID } from "node:crypto";
import { CsvImportError } from "../../errors.ts";
import { clientesRequestSchema, validate } from "../../schemas/index.ts";
import type { SortSpec } from "./clientes.repo.ts";
import * as service from "./clientes.service.ts";
import { processCsvFile } from "./csv.service.ts";

export const clientesRoutes = new Hono();

const VALID_CSV_CONTENT_TYPES = new Set(["text/csv", "application/vnd.ms-excel", "text/plain"]);

function parsePagination(c: { req: { query: (k: string) => string | undefined; queries: (k: string) => string[] | undefined } }) {
  const pageRaw = Number(c.req.query("page") ?? "0");
  const sizeRaw = Number(c.req.query("size") ?? "10");
  const page = Number.isFinite(pageRaw) && pageRaw >= 0 ? Math.trunc(pageRaw) : 0;
  let size = Number.isFinite(sizeRaw) && sizeRaw >= 1 ? Math.trunc(sizeRaw) : 10;
  if (size > 100) size = 100;

  const sortSpecs: SortSpec[] = [];
  for (const raw of c.req.queries("sort") ?? []) {
    const [field, dir] = raw.split(",");
    if (field) {
      sortSpecs.push({ field: field as SortSpec["field"], dir: dir === "desc" ? "desc" : "asc" });
    }
  }
  return { page, size, sortSpecs };
}

// POST /import — recebe CSV, valida e inicia o processamento em segundo plano (202).
clientesRoutes.post("/import", async (c) => {
  const body = await c.req.parseBody();
  const file = body["file"];

  if (!(file instanceof File) || file.size === 0) {
    throw new CsvImportError("O arquivo CSV está vazio.");
  }

  const filename = file.name;
  if (!filename || !filename.toLowerCase().endsWith(".csv")) {
    throw new CsvImportError("O arquivo deve ter a extensão .csv");
  }

  const contentType = file.type;
  if (contentType && !VALID_CSV_CONTENT_TYPES.has(contentType)) {
    throw new CsvImportError("Formato de arquivo inválido.");
  }

  const tempPath = join(tmpdir(), `import-${randomUUID()}.csv`);
  await Bun.write(tempPath, file);

  // Fire-and-forget: processa em segundo plano (Railway é container persistente).
  void processCsvFile(tempPath);

  return c.text("Importação iniciada em segundo plano.", 202);
});

// POST / — cadastra um cliente manualmente.
clientesRoutes.post("/", async (c) => {
  const dto = validate(clientesRequestSchema, await c.req.json());
  await service.saveCliente(dto);
  return c.body(null, 200);
});

// GET /instalacao/:instalacao
clientesRoutes.get("/instalacao/:instalacao", async (c) => {
  const instalacao = Number(c.req.param("instalacao"));
  if (!Number.isFinite(instalacao)) throw new CsvImportError("Número da instalação inválido.");
  return c.json(await service.findByInstalacao(instalacao));
});

// GET /conta-contrato/:contaContrato
clientesRoutes.get("/conta-contrato/:contaContrato", async (c) => {
  const conta = Number(c.req.param("contaContrato"));
  if (!Number.isFinite(conta)) throw new CsvImportError("Conta contrato inválida.");
  const { page, size, sortSpecs } = parsePagination(c);
  return c.json(await service.findByContaContrato(conta, page, size, sortSpecs));
});

// GET /numero-serie/:numeroSerie
clientesRoutes.get("/numero-serie/:numeroSerie", async (c) => {
  const numeroSerie = Number(c.req.param("numeroSerie"));
  if (!Number.isFinite(numeroSerie)) throw new CsvImportError("Número de série inválido.");
  const { page, size, sortSpecs } = parsePagination(c);
  return c.json(await service.findByNumeroSerie(numeroSerie, page, size, sortSpecs));
});

// GET /numero-poste/:numeroPoste
clientesRoutes.get("/numero-poste/:numeroPoste", async (c) => {
  const numeroPoste = c.req.param("numeroPoste");
  const { page, size, sortSpecs } = parsePagination(c);
  return c.json(await service.findByNumeroPoste(numeroPoste, page, size, sortSpecs));
});
