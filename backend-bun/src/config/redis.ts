import { RedisClient } from "bun";
import { redisConnectionUrl } from "./env.ts";

// Cliente Redis nativo do Bun (baixo overhead). Conecta de forma preguiçosa.
export const redis = new RedisClient(redisConnectionUrl());

/** GET simples; retorna null se a chave não existir. */
export async function redisGet(key: string): Promise<string | null> {
  const result = await redis.send("GET", [key]);
  return (result as string | null) ?? null;
}

/** SET com expiração em segundos (replica o TTL do Spring Cache). */
export async function redisSetEx(key: string, value: string, ttlSeconds: number): Promise<void> {
  await redis.send("SET", [key, value, "EX", String(ttlSeconds)]);
}

/** Remove uma chave específica. */
export async function redisDel(key: string): Promise<void> {
  await redis.send("DEL", [key]);
}

/**
 * Remove todas as chaves que casam com um padrão (ex.: "equipes:*").
 * Usa SCAN para não bloquear o servidor Redis.
 */
export async function redisDelByPattern(pattern: string): Promise<void> {
  let cursor = "0";
  do {
    const reply = (await redis.send("SCAN", [cursor, "MATCH", pattern, "COUNT", "200"])) as [
      string,
      string[],
    ];
    cursor = reply[0];
    const keys = reply[1];
    if (keys.length > 0) {
      await redis.send("DEL", keys);
    }
  } while (cursor !== "0");
}
