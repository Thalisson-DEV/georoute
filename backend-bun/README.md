# GeoRoute Backend (Bun + TypeScript)

Reescrita do back-end GeoRoute (originalmente Spring Boot 3 / Java 21) como um **monolito em Bun + TypeScript**, com foco em **reduzir o consumo de RAM** e **preservar os contratos HTTP** (o front-end Angular não precisa mudar, exceto pela remoção de autenticação).

## Stack
- **Runtime:** Bun + TypeScript
- **HTTP:** [Hono](https://hono.dev)
- **Banco:** PostgreSQL (mesmo banco do Railway) via **Drizzle ORM** + driver `postgres`
- **Cache:** Redis (cliente nativo do Bun)
- **Validação:** `zod`
- **CSV:** `csv-parse` em modo streaming
- **Cron:** agendador interno (limpeza diária de rotas)

## O que mudou em relação ao back-end Java
- **Removidos:** autenticação (JWT, `/auth/login`, `/user/register`, tabela `users`) e observabilidade (Micrometer/Prometheus/Actuator/Swagger).
- **Mantidos (contratos idênticos):** clientes, equipes, rotas (ORS), mapas e importação CSV.
- **Otimizações:** histórico de rotas não carrega mais o campo `rota_json` (grande); CSV processado em streaming + upsert em lote de 1000 dentro de uma transação; novos índices de banco; cache em Redis. Corrigido o bug de invalidação de cache do import (antes limpava um cache inexistente `"cliente"`).

## Variáveis de ambiente
Copie `.env.example` para `.env`. Reaproveita as variáveis já existentes no Railway:

| Variável | Descrição |
|---|---|
| `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD` | PostgreSQL |
| `REDISHOST`, `REDISPORT`, `REDISUSER`, `REDISPASSWORD` | Redis |
| `ORS_API_KEY` | Chave do OpenRouteService |
| `FRONTEND_URL` | Origem do front-end (CORS) |
| `PORT` | Porta do servidor (padrão 8080) |

> `JWT_SECRET` não é mais necessária.

## Desenvolvimento
```bash
bun install
bun run migrate   # aplica os índices aditivos (idempotente)
bun run dev       # servidor com hot reload
```

## Scripts
- `bun run dev` — servidor com `--watch`
- `bun run start` — produção
- `bun run migrate` — aplica `src/db/migrations/*.sql` (idempotente, `CREATE INDEX IF NOT EXISTS`)
- `bun run typecheck` — checagem de tipos

## Migrations / Índices
O schema das tabelas **já existe** no Railway (criado via Flyway no projeto Java). Este projeto **não recria tabelas**; apenas adiciona índices de forma idempotente em `src/db/migrations/0001_indexes.sql`:
- `execucoes_rota (equipe_id, data DESC)` e `execucoes_rota (data)`
- `equipes (setor, municipio)`, `equipes (municipio)`, `equipes (nome)`

## Deploy (Railway)
Build via `Dockerfile` (`oven/bun:1`). O `railway.json` define `healthcheckPath: /health`. Configure as variáveis de ambiente no painel e rode `bun run migrate` uma vez (ou antes do cutover).

## Endpoints (base `/api/v1`)
- `clientes`: `POST /import` (multipart, 202), `POST /`, `GET /instalacao/:id`, `GET /conta-contrato/:id`, `GET /numero-serie/:id`, `GET /numero-poste/:id`
- `equipes`: `POST /` (201), `PUT /:id`, `DELETE /:id` (204), `GET /?setor=&municipio=`
- `routes`: `POST /optimize`, `GET /history/:teamId`, `GET /:id`
- `maps`: `GET /redirect?latitude=&longitude=` (302)
- `GET /health` (substitui `/actuator/health`)

## Impacto no front-end (autenticação removida)
Como a autenticação foi removida, os artefatos de auth do front-end ficam órfãos e devem ser ajustados no cutover: remover `authGuard` das rotas, o `auth.interceptor`, `auth.service` e a página de login. Os demais endpoints permanecem idênticos.
