FROM oven/bun:1

WORKDIR /app

# Instala dependências primeiro (cache de camada)
COPY package.json bun.lock* ./
RUN bun install --frozen-lockfile || bun install

# Copia o restante do código
COPY . .

ENV PORT=8080
EXPOSE 8080

CMD ["bun", "run", "src/index.ts"]
