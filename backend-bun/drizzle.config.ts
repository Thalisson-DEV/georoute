import { defineConfig } from "drizzle-kit";

// Usado apenas para introspecção/geração opcional.
// O schema do banco já existe no Railway (criado via Flyway no back-end Java),
// portanto NÃO usamos `drizzle-kit push`. Migrations são aplicadas por src/db/migrate.ts.
export default defineConfig({
  dialect: "postgresql",
  schema: "./src/db/schema.ts",
  out: "./src/db/migrations",
  dbCredentials: {
    host: process.env.PGHOST ?? "localhost",
    port: Number(process.env.PGPORT ?? 5432),
    database: process.env.PGDATABASE ?? "georoute-api",
    user: process.env.PGUSER ?? "postgres",
    password: process.env.PGPASSWORD ?? "postgres",
    ssl: false,
  },
});
