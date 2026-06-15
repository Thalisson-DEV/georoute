import { Hono } from "hono";
import { AppError } from "../../errors.ts";
import { equipesRequestSchema, municipioEnum, setorEnum, validate } from "../../schemas/index.ts";
import type { Municipio, Setor } from "../../schemas/index.ts";
import * as service from "./equipes.service.ts";

export const equipesRoutes = new Hono();

function parseEnumParam<T>(value: string | undefined, parser: { safeParse: (v: unknown) => { success: boolean; data?: T } }, label: string): T | undefined {
  if (value === undefined || value === "") return undefined;
  const result = parser.safeParse(value);
  if (!result.success) throw new AppError(`Valor inválido para ${label}: ${value}`, 400);
  return result.data;
}

// POST / — cadastra equipe (201).
equipesRoutes.post("/", async (c) => {
  const dto = validate(equipesRequestSchema, await c.req.json());
  await service.saveEquipe(dto);
  return c.body(null, 201);
});

// PUT /:id — atualiza equipe (200).
equipesRoutes.put("/:id", async (c) => {
  const id = Number(c.req.param("id"));
  if (!Number.isFinite(id)) throw new AppError("ID inválido", 400);
  const dto = validate(equipesRequestSchema, await c.req.json());
  await service.updateEquipe(id, dto);
  return c.body(null, 200);
});

// DELETE /:id — remove equipe (204).
equipesRoutes.delete("/:id", async (c) => {
  const id = Number(c.req.param("id"));
  if (!Number.isFinite(id)) throw new AppError("ID inválido", 400);
  await service.deleteEquipe(id);
  return c.body(null, 204);
});

// GET / — lista equipes, filtrando opcionalmente por setor e/ou município (200).
equipesRoutes.get("/", async (c) => {
  const setor = parseEnumParam<Setor>(c.req.query("setor"), setorEnum, "setor");
  const municipio = parseEnumParam<Municipio>(c.req.query("municipio"), municipioEnum, "municipio");
  return c.json(await service.findAllEquipes(setor, municipio));
});
