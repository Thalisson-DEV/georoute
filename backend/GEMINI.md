# Sipel Logistics Helper

Este documento serve como guia central para o desenvolvimento e manutenção do projeto Sipel Logistics Helper.

## 1. Visão Geral
O projeto é uma aplicação Full Stack destinada a auxiliar em processos logísticos. O sistema permite o cadastro e consulta de informações de clientes (instalação, conta contrato, geolocalização), gerenciamento de equipes e otimização de rotas utilizando a API OpenRouteService.

### Tecnologias Principais
*   **Backend:** Java 21, Spring Boot 3.4.2, Spring Data JPA, Flyway, MapStruct, PostgreSQL, Redis, Spring Security (JWT), WebClient.
*   **Frontend:** Angular 19+ (Standalone Components, Material Design).
*   **Integrações:** OpenRouteService (Otimização de rotas).

## 2. Arquitetura do Backend
O backend segue uma arquitetura em camadas clássica:

1.  **Controllers (web):** Camada REST que recebe as requisições HTTP.
    *   `ClientesController`: CRUD e busca de clientes.
    *   `EquipesController`: CRUD de equipes (com filtro por setor).
    *   `RouteController`: Endpoint para cálculo e otimização de rotas.
    *   `MapsController`: Redirecionamentos para serviços de mapa.
    *   `AuthenticationController`: Login e JWT.
2.  **Services (service):** Lógica de negócios.
    *   `RouteService`: Integração com ORS, validação de dados e cacheamento de rotas.
    *   `EquipesService`: Regras de negócio para gestão de equipes.
3.  **Domain (domain):** Entidades JPA e DTOs.
    *   Novas Entidades: `Equipes` (com Enum `SetorEnum`) e `ExecucoesRota`.
4.  **Repositories (repository):** Interface com o banco de dados.
5.  **Mappers:** Conversão entre Entidades e DTOs (MapStruct).
    *   `RouteMapper` e `EquipesMapper`.
6.  **Security (infra/security):** Segurança Stateless com JWT.

### Banco de Dados & Cache
*   **Schema:** Gerenciado via Flyway (`V1` a `V4`).
*   **Rotas:** Tabela `execucoes_rota` armazena histórico de rotas geradas.
*   **Cache:** Redis utilizado para armazenar rotas calculadas (TTL 24h) e consultas de clientes.

## 3. Configuração e Execução (Backend)

### Variáveis de Ambiente
Para utilizar a otimização de rotas, é necessário configurar a chave da API:
*   `ORS_API_KEY`: Chave válida do OpenRouteService.

### Executando a Aplicação
`ash
export ORS_API_KEY=sua_chave_aqui
./mvnw clean install
./mvnw spring-boot:run
`

## 4. Documentação da API

### Rotas e Otimização
*   `POST /api/v1/routes/optimize`: Calcula a melhor rota para uma lista de clientes.
    *   Requer `teamId`. Se `currentLat/Lon` não forem enviados, usa a base da equipe.
    *   Cacheado por dia e equipe.

### Equipes
*   `POST /api/v1/equipes`: Cadastra nova equipe.
*   `GET /api/v1/equipes`: Lista equipes (opcional `?setor=LEITURA`).
*   `PUT /api/v1/equipes/{id}`: Atualiza dados da equipe.
*   `DELETE /api/v1/equipes/{id}`: Remove equipe.

### Clientes
*   Endpoints de busca por instalação, conta contrato, etc.

## 5. Frontend (Angular)
(Seção mantida conforme original)

---
*Gerado pelo Assistente Gemini CLI*
