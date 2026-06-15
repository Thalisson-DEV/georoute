# 🚛 Sipel Logistics Helper (GeoRoute)

<p align="center">
  <img src="https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow?style=for-the-badge" alt="Status">
  <img src="https://img.shields.io/badge/Licença-Sipel%20Construções-blue?style=for-the-badge" alt="License">
</p>

## 📋 Visão Geral

O **Sipel Logistics Helper** (codinome GeoRoute) é uma solução Full Stack corporativa desenvolvida para otimizar as operações logísticas da **Sipel Construções LTDA**. O sistema centraliza dados críticos de clientes e infraestrutura, integrando-os a serviços de geolocalização para facilitar o planejamento de rotas e atendimento em campo.

### 🎯 Objetivos Principais
- **Centralização:** Base única para consulta de Instalações, Contas Contrato e Ativos de Rede (Postes).
- **Agilidade:** Busca unificada e rápida com detecção automática do tipo de dado.
- **Geolocalização:** Integração direta com Google Maps para navegação até o ponto de serviço.
- **Importação:** Processamento assíncrono de grandes volumes de dados via CSV.

### 🗺️ Funcionalidades de Roteirização (Novo)
O sistema agora conta com um poderoso **Planejador de Rotas** para otimizar o trabalho de campo:
- **Otimização Inteligente:** Criação de rotas otimizadas com base na localização das equipes e dos clientes selecionados.
- **Histórico de Rotas:** Consulta completa de rotas anteriores por equipe, permitindo auditoria e replanejamento.
- **Integração com Maps:** Geração automática de links de navegação turn-by-turn para o Google Maps.
- **Flexibilidade:** Suporte para início de rota a partir da Base Operacional ou da Localização Atual (GPS) do dispositivo.

## 🏗️ Estrutura do Projeto

O projeto é dividido em dois grandes módulos monorepo:

### 🔙 [Backend](./backend-bun)
API RESTful leve construída com **Bun** e **TypeScript** (monolito).
- **Stack:** [Hono](https://hono.dev) (HTTP), **Drizzle ORM** + PostgreSQL, cache em **Redis** (cliente nativo do Bun), validação com `zod` e import de CSV em streaming.
- **Destaques:** baixo consumo de RAM, agendador interno de limpeza de rotas e os mesmos contratos HTTP do back-end anterior (base `/api/v1`).
- > Reescrito a partir do antigo back-end Java 21 / Spring Boot. A **autenticação foi removida** (não há mais `/auth/login`, JWT ou tabela de usuários), assim como a observabilidade (Prometheus/Actuator/Swagger).
- [Ver Documentação do Backend](./backend-bun/README.md)

### 🖥️ [Frontend](./frontend)
Interface moderna e responsiva construída com **Angular 21**.
- **Destaques:** Standalone Components, TailwindCSS, e PWA ready.
- [Ver Documentação do Frontend](./frontend/README.md)

## 🚀 Como Executar o Projeto Completo

### Pré-requisitos
- [Bun](https://bun.sh) (v1+)
- Node.js (v20+) — para o front-end
- Docker (opcional) — para subir PostgreSQL e Redis localmente

### Infraestrutura (Banco de Dados & Cache)
O back-end usa **PostgreSQL** e **Redis**. Em desenvolvimento, suba-os via Docker (ou aponte para instâncias existentes):

```bash
docker run -d --name georoute-pg -e POSTGRES_PASSWORD=postgres -p 5432:5432 postgres:16
docker run -d --name georoute-redis -p 6379:6379 redis:7
```

### Rodando o Backend
```bash
cd backend-bun
cp .env.example .env   # configure PG*, REDIS*, ORS_API_KEY
bun install
bun run migrate        # aplica índices (idempotente)
bun run dev            # servidor com hot reload
```
*API disponível em: http://localhost:8080*

### Rodando o Frontend
```bash
cd frontend
npm install
npm start
```
*Aplicação disponível em: http://localhost:4200*

## 📄 Licença e Direitos Autorais

Copyright © 2026 **Sipel Construções LTDA**. Todos os direitos reservados.

Este software é de propriedade exclusiva da Sipel Construções LTDA. O uso, cópia, modificação ou distribuição não autorizada deste software, ou de qualquer parte dele, é estritamente proibida, exceto conforme permitido pela licença incluída.

Desenvolvido pelo mantenedor do projeto para atender às necessidades logísticas da empresa.

---
<p align="center">
  <strong>Sipel Construções LTDA</strong><br>
  Soluções em Engenharia e Logística
</p>
