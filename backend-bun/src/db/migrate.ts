// Runner de migrations ADITIVAS e idempotentes.
// Aplica todos os arquivos .sql de src/db/migrations em ordem alfabética.
// Seguro para rodar múltiplas vezes (usa IF NOT EXISTS).
import { readdir } from "node:fs/promises";
import { join } from "node:path";
import { sql } from "../config/db.ts";

const migrationsDir = join(import.meta.dir, "migrations");

async function run() {
  const files = (await readdir(migrationsDir))
    .filter((f) => f.endsWith(".sql"))
    .sort();

  if (files.length === 0) {
    console.log("Nenhuma migration encontrada.");
    return;
  }

  for (const file of files) {
    const path = join(migrationsDir, file);
    const content = await Bun.file(path).text();
    console.log(`Aplicando migration: ${file}`);
    await sql.unsafe(content);
  }

  console.log("Migrations aplicadas com sucesso.");
}

run()
  .then(() => sql.end())
  .then(() => process.exit(0))
  .catch((err) => {
    console.error("Falha ao aplicar migrations:", err);
    sql.end().finally(() => process.exit(1));
  });
