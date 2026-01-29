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

## 🏗️ Estrutura do Projeto

O projeto é dividido em dois grandes módulos monorepo:

### 🔙 [Backend](./backend)
API RESTful robusta construída com **Java 21** e **Spring Boot**.
- **Destaques:** Alta performance com Redis, segurança com JWT/Spring Security, e monitoramento com Prometheus/Grafana.
- [Ver Documentação do Backend](./backend/README.md)

### 🖥️ [Frontend](./frontend)
Interface moderna e responsiva construída com **Angular 21**.
- **Destaques:** Standalone Components, TailwindCSS, e PWA ready.
- [Ver Documentação do Frontend](./frontend/README.md)

## 🚀 Como Executar o Projeto Completo

### Pré-requisitos
- Docker & Docker Compose
- Node.js (v20+)
- Java JDK 21

### Infraestrutura (Banco de Dados & Cache)
O backend possui um `docker-compose.yaml` para subir as dependências rapidamente.

```bash
cd backend
docker-compose up -d
```

### Rodando o Backend
```bash
cd backend
./mvnw spring-boot:run
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
