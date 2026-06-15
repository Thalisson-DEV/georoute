// Helpers de cache sobre o Redis, replicando a semântica do @Cacheable do Spring:
// - TTL padrão de 60 minutos
// - valores serializados como JSON
// - não cacheia valores nulos/undefined (disableCachingNullValues)
import { redisGet, redisSetEx, redisDelByPattern } from "../config/redis.ts";

export const DEFAULT_TTL_SECONDS = 60 * 60; // 60 minutos (igual ao CacheConfig do Java)

/**
 * Retorna o valor cacheado (desserializado) ou executa `loader`, cacheando o resultado.
 * Não cacheia null/undefined. Se o Redis falhar, faz fallback para o loader.
 */
export async function getOrSet<T>(
  key: string,
  loader: () => Promise<T>,
  ttlSeconds: number = DEFAULT_TTL_SECONDS,
): Promise<T> {
  try {
    const cached = await redisGet(key);
    if (cached !== null) {
      return JSON.parse(cached) as T;
    }
  } catch (err) {
    console.error(`[cache] erro ao ler ${key}:`, err);
  }

  const value = await loader();

  if (value !== null && value !== undefined) {
    try {
      await redisSetEx(key, JSON.stringify(value), ttlSeconds);
    } catch (err) {
      console.error(`[cache] erro ao gravar ${key}:`, err);
    }
  }

  return value;
}

/** Invalida todas as chaves de um "cache" lógico (ex.: prefixo "equipes:"). */
export async function evictByPrefix(prefix: string): Promise<void> {
  try {
    await redisDelByPattern(`${prefix}*`);
  } catch (err) {
    console.error(`[cache] erro ao invalidar prefixo ${prefix}:`, err);
  }
}
