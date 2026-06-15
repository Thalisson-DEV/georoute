import { drizzle } from "drizzle-orm/postgres-js";
import postgres from "postgres";
import { env } from "./env.ts";
import * as schema from "../db/schema.ts";

// Pool de conexões enxuto para reduzir uso de memória/conexões.
// max: 10 conexões é suficiente para a carga atual (equivalente às ~20 threads do Tomcat).
export const sql = postgres({
  host: env.pg.host,
  port: env.pg.port,
  database: env.pg.database,
  username: env.pg.user,
  password: env.pg.password,
  max: 10,
  idle_timeout: 20,
  max_lifetime: 60 * 30,
  // Railway exige SSL em produção; aceita certificado autoassinado.
  ssl: env.pg.host.includes("localhost") || env.pg.host === "127.0.0.1" ? false : "require",
});

export const db = drizzle(sql, { schema });

export type Database = typeof db;
