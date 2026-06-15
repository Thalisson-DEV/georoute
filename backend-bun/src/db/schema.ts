// Schema Drizzle que ESPELHA as tabelas já existentes no PostgreSQL (criadas via Flyway).
// IMPORTANTE: não usar `drizzle-kit push` — o banco é a fonte da verdade.
import {
  bigint,
  bigserial,
  date,
  doublePrecision,
  pgTable,
  text,
  varchar,
} from "drizzle-orm/pg-core";

export const clientes = pgTable("clientes", {
  instalacao: bigint("instalacao", { mode: "number" }).primaryKey(),
  contaContrato: bigint("conta_contrato", { mode: "number" }),
  numeroSerie: bigint("numero_serie", { mode: "number" }),
  numeroPoste: varchar("numero_poste", { length: 100 }),
  nomeCliente: varchar("nome_cliente", { length: 200 }),
  latitude: doublePrecision("latitude"),
  longitude: doublePrecision("longitude"),
});

export const equipes = pgTable("equipes", {
  id: bigserial("id", { mode: "number" }).primaryKey(),
  nome: varchar("nome", { length: 255 }),
  latitudeBase: doublePrecision("latitude_base"),
  longitudeBase: doublePrecision("longitude_base"),
  setor: varchar("setor", { length: 50 }),
  municipio: varchar("municipio", { length: 255 }).notNull(),
});

export const execucoesRota = pgTable("execucoes_rota", {
  id: varchar("id", { length: 255 }).primaryKey(),
  equipeId: bigint("equipe_id", { mode: "number" }),
  data: date("data"),
  rotaJson: text("rota_json"),
});

export type ClienteRow = typeof clientes.$inferSelect;
export type ClienteInsert = typeof clientes.$inferInsert;
export type EquipeRow = typeof equipes.$inferSelect;
export type EquipeInsert = typeof equipes.$inferInsert;
export type ExecucaoRotaRow = typeof execucoesRota.$inferSelect;
export type ExecucaoRotaInsert = typeof execucoesRota.$inferInsert;
