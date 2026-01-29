# 🚚 GeoRoute API

<p align="center">
  <a href="#-sobre-o-projeto">Sobre</a> •
  <a href="#-tecnologias-utilizadas">Tecnologias</a> •
  <a href="#-arquitetura-e-estrutura">Arquitetura</a> •
  <a href="#-instalação-e-configuração">Instalação</a> •
  <a href="#-documentação-interativa-swagger">Documentação</a> •
  <a href="#-endpoints-da-api">Endpoints</a> •
  <a href="#-observabilidade">Observabilidade</a>
</p>

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green?style=for-the-badge&logo=spring&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white)
![Grafana](https://img.shields.io/badge/grafana-%23F46800.svg?style=for-the-badge&logo=grafana&logoColor=white)

## 📖 Sobre o Projeto

Esta é uma API RESTful robusta desenvolvida para auxiliar em processos logísticos da Sipel. O sistema centraliza o cadastro e consulta de informações de clientes (instalação, conta contrato, geolocalização) e oferece integração inteligente para redirecionamento de rotas.

O projeto foi desenhado com foco em **Alta Disponibilidade** e **Observabilidade**, incluindo suporte nativo a métricas de negócio.

## 🚀 Tecnologias Utilizadas

O projeto utiliza uma stack moderna baseada no ecossistema Spring:

- **Java 21**: Linguagem base (LTS).
- **Spring Boot**: Framework principal para desenvolvimento ágil.
- **Spring Data JPA**: Abstração de persistência de dados.
- **Flyway**: Versionamento e migração de banco de dados.
- **MapStruct**: Mapeamento performático entre Entidades e DTOs.
- **SpringDoc OpenAPI (Swagger)**: Documentação interativa e padronizada da API.
- **Redis**: Caching distribuído para alta performance.
- **OpenCSV**: Processamento assíncrono de grandes volumes de dados (Importação).
- **Micrometer/Prometheus**: Coleta de métricas de aplicação e negócios.
- **Docker & Docker Compose**: Orquestração de containers (DB, Cache, Monitoramento).

## ⚙️ Arquitetura e Estrutura

A aplicação segue uma arquitetura em camadas clássica e limpa:

```text
src/main/java/com/sipel/backend/
├── controllers/         # Endpoints REST (Exposição)
├── services/            # Lógica de Negócios
├── domain/              # Entidades JPA
├── repositories/        # Acesso a Dados (Spring Data)
├── mappers/             # Conversores (Entity <-> DTO)
├── dtos/                # Objetos de Transferência de Dados
└── infra/               # Infraestrutura (CSV, Configurações, Exceptions)
```

## 🛠️ Instalação e Configuração

### 🚀 Produção (Railway)

Para o deploy no Railway:
1. A aplicação utiliza o perfil `prod` via `Procfile`.
2. Utilize os **Add-ons nativos** do Railway para PostgreSQL e Redis.
3. A observabilidade é feita de forma nativa pelo painel do Railway (Metrics/Logs).
4. O `docker-compose.yaml` é ignorado no deploy da API.

### 💻 Desenvolvimento Local

#### Pré-requisitos
- **Java 21** instalado.
- **Docker** e **Docker Compose** instalados.

#### Passo a Passo

1. **Clone o repositório**
   ```bash
   git clone <url-do-repositorio>
   cd backend
   ```

2. **Suba a Infraestrutura Local**
   O projeto utiliza Docker para gerenciar dependências externas em ambiente de desenvolvimento.
   ```bash
   docker-compose up -d
   ```
   *Nota: Certifique-se de ter uma instância PostgreSQL rodando localmente na porta 5432 ou ajuste o `docker-compose.yaml` para incluir o banco.*

3. **Configuração de Variáveis de Ambiente**
   Configure as credenciais do banco de dados no seu ambiente ou em um arquivo `.env` (se configurado).
   
   **Linux/Mac:**
   ```bash
   export DB_USERNAME=seu_usuario
   export DB_PASSWORD=sua_senha
   ```

   **Windows (PowerShell):**
   ```powershell
   $env:DB_USERNAME="seu_usuario"
   $env:DB_PASSWORD="sua_senha"
   ```

4. **Compile e Execute**
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

   A API iniciará em `http://localhost:8080`.

## 📚 Documentação Interativa (Swagger)

A API possui documentação completa via **Swagger UI**, permitindo testar requisições diretamente pelo navegador e visualizar os schemas de dados.

- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **JSON Docs**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

Utilize essa interface para entender os parâmetros necessários, formatos de resposta e códigos de erro de cada endpoint.

## 🔌 Endpoints da API

### 👤 Clientes
Gerenciamento de dados dos clientes e instalações.

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/v1/clientes` | Cadastra um novo cliente |
| `POST` | `/api/v1/clientes/import` | Importação em massa via arquivo CSV (Async) |
| `GET` | `/api/v1/clientes/instalacao/{id}` | Busca por Número de Instalação (Cache Individual) |
| `GET` | `/api/v1/clientes/conta-contrato/{id}` | Busca por Conta Contrato (Paginado & Cacheado) |
| `GET` | `/api/v1/clientes/numero-serie/{id}` | Busca por Número de Série (Paginado & Cacheado) |
| `GET` | `/api/v1/clientes/numero-poste/{id}` | Busca por Identificador do Poste (Paginado & Cacheado) |

### 🔐 Autenticação & Usuários
Gerenciamento de acesso e tokens.

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/v1/auth/login` | Autentica um usuário e retorna um token JWT |
| `POST` | `/api/v1/user/register` | Cadastra um novo usuário |

### 🗺️ Mapas
Integração com serviços de geolocalização.

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/v1/maps/redirect` | Redireciona para o Google Maps com base nas coordenadas |

## 📊 Observabilidade

O projeto já nasce instrumentado para monitoramento.

- **Grafana:** `http://localhost:3000` (Visualize dashboards de performance e métricas de negócio).
- **Prometheus:** `http://localhost:9090`.
- **Métricas de Negócio:** Acompanhe o volume de consultas por tipo (`business.clientes.consultas`).

## 📝 Roadmap & TODO

O projeto está em evolução. As seguintes melhorias estão planejadas:

- [x] **Documentação:** Implementar Swagger UI / OpenAPI para documentação interativa das rotas e schemas.
- [ ] **Segurança:** Adicionar camada de segurança (Spring Security) para proteger as rotas de escrita (`POST /clientes` e `importação`), exigindo autenticação.

## 🤝 Contribuição

1. Faça um Fork do projeto
2. Crie uma Branch para sua Feature (`git checkout -b feature/NovaFeature`)
3. Faça o Commit (`git commit -m 'Add: nova funcionalidade'`)
4. Faça o Push (`git push origin feature/NovaFeature`)
5. Abra um Pull Request

---
<p align="center">
  Desenvolvido para <strong>Sipel Construções LTDA</strong>
</p>