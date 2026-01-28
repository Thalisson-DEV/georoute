# Sipel Logistics Helper

Este documento serve como guia central para o desenvolvimento e manutenção do projeto Sipel Logistics Helper.

## 1. Visão Geral
O projeto é uma aplicação Full Stack destinada a auxiliar em processos logísticos. O sistema permite o cadastro e consulta de informações de clientes (instalação, conta contrato, geolocalização) e oferece integração com o Google Maps para rotas.

### Tecnologias Principais
*   **Backend:** Java 21, Spring Boot, Spring Data JPA, Flyway, MapStruct, PostgreSQL, Redis.
*   **Frontend (Planejado):** Angular 20+ (Utilizando versões mais recentes estáveis, ex: v17/v18+).

## 2. Arquitetura do Backend
O backend segue uma arquitetura em camadas clássica:

1.  **Controllers (`web`):** Camada REST que recebe as requisições HTTP.
    *   `ClientesController`: Gerencia operações de CRUD e busca de clientes.
    *   `MapsController`: Gerencia redirecionamentos para serviços de mapa.
2.  **Services (`service`):** Contém a lógica de negócios.
3.  **Domain (`domain`):** Entidades JPA e DTOs.
4.  **Repositories (`repository`):** Interface com o banco de dados via Spring Data JPA.
5.  **Mappers:** Conversão entre Entidades e DTOs usando MapStruct.

### Banco de Dados
*   **Gerenciamento de Schema:** Flyway (`src/main/resources/db/migration`).
*   **Banco Principal:** PostgreSQL.
*   **Cache:** Redis (dependência incluída para otimização futura).

## 3. Configuração e Execução (Backend)

### Pré-requisitos
*   JDK 21 instalado.
*   Maven instalado.
*   PostgreSQL rodando (localmente ou via Docker).
*   Redis rodando (opcional, dependendo da configuração ativa).

### Configuração do Banco de Dados
Certifique-se de que o arquivo `src/main/resources/application.yaml` (ou o perfil ativo `application-dev.yaml`) aponta para uma instância válida do PostgreSQL.

Exemplo de configuração esperada (ajuste conforme seu ambiente):
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/sipel_db
    username: seu_usuario
    password: sua_senha
```

### Executando a Aplicação
No diretório raiz do projeto:

```bash
# Compilar e baixar dependências
./mvnw clean install

# Rodar a aplicação
./mvnw spring-boot:run
```

## 4. Documentação da API

### Clientes
*   `POST /api/v1/clientes`: Cadastra um novo cliente.
*   `GET /api/v1/clientes/instalacao/{id}`: Busca por número de instalação.
*   `GET /api/v1/clientes/conta-contrato/{id}`: Busca por conta contrato.
*   `GET /api/v1/clientes/numero-serie/{id}`: Busca por número de série.
*   `GET /api/v1/clientes/numero-poste/{id}`: Busca por número do poste.

### Maps
*   `GET /api/v1/maps/redirect`: Recebe latitude/longitude no corpo e redireciona para o Google Maps.

## 5. Próximos Passos (Frontend & Integração)

O próximo grande marco é o desenvolvimento do frontend em Angular.

### Estrutura Sugerida para o Frontend
1.  **Criação do Projeto:** `ng new frontend --style=scss --routing`
2.  **Bibliotecas Recomendadas:**
    *   Angular Material (UI Components).
    *   Leaflet ou Google Maps API (para visualização de mapas embutida, se necessário).
3.  **Funcionalidades a Implementar:**
    *   **Formulário de Cadastro:** Interface para inputar os dados do cliente (Instalação, Nome, Coordenadas, etc).
    *   **Busca:** Campo de pesquisa para encontrar clientes por diferentes chaves (Instalação, Poste, etc).
    *   **Detalhes & Ação:** Exibir dados do cliente e botão "Ver no Mapa" que aciona o endpoint de redirecionamento ou abre o mapa diretamente.

### Ajustes Necessários no Backend
*   **CORS:** Configurar `WebMvcConfigurer` para permitir requisições vindas do localhost do Angular (geralmente porta 4200).
*   **Validação de Versão:** Verificar a versão do Spring Boot no `pom.xml`. A versão `4.0.2` listada pode não existir (a atual estável é a linha 3.x). Recomenda-se ajustar para uma versão LTS recente (ex: 3.4.x).

---
*Gerado pelo Assistente Gemini CLI*
