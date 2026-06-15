// Leitura e validação das variáveis de ambiente (reaproveita as do Railway).
function required(name: string): string {
  const value = process.env[name];
  if (value === undefined || value === "") {
    throw new Error(`Variável de ambiente obrigatória ausente: ${name}`);
  }
  return value;
}

function optional(name: string, fallback: string): string {
  const value = process.env[name];
  return value === undefined || value === "" ? fallback : value;
}

export const env = {
  pg: {
    host: optional("PGHOST", "localhost"),
    port: Number(optional("PGPORT", "5432")),
    database: optional("PGDATABASE", "georoute-api"),
    user: optional("PGUSER", "postgres"),
    password: optional("PGPASSWORD", "postgres"),
  },
  redis: {
    host: optional("REDISHOST", "localhost"),
    port: Number(optional("REDISPORT", "6379")),
    user: process.env.REDISUSER ?? "",
    password: process.env.REDISPASSWORD ?? "",
  },
  ors: {
    url: optional("ORS_URL", "https://api.openrouteservice.org/optimization"),
    apiKey: process.env.ORS_API_KEY ?? "",
  },
  frontendUrl: optional("FRONTEND_URL", "http://localhost:4200"),
  port: Number(optional("PORT", "8080")),
};

export function pgConnectionString(): string {
  const { host, port, database, user, password } = env.pg;
  const auth = `${encodeURIComponent(user)}:${encodeURIComponent(password)}`;
  return `postgres://${auth}@${host}:${port}/${database}`;
}

export function redisConnectionUrl(): string {
  const { host, port, user, password } = env.redis;
  if (password) {
    const auth = `${encodeURIComponent(user)}:${encodeURIComponent(password)}`;
    return `redis://${auth}@${host}:${port}`;
  }
  return `redis://${host}:${port}`;
}

// Exporta a função usada acima para validar variáveis críticas em runtime, se necessário.
export { required };
