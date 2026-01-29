# 💻 GeoRoute Frontend

<p align="center">
  <a href="#-sobre-o-projeto">Sobre</a> •
  <a href="#-tecnologias-utilizadas">Tecnologias</a> •
  <a href="#-funcionalidades">Funcionalidades</a> •
  <a href="#-instalação-e-execução">Instalação</a> •
  <a href="#-estrutura-do-projeto">Estrutura</a>
</p>

![Angular](https://img.shields.io/badge/Angular-21-DD0031?style=for-the-badge&logo=angular&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-007ACC?style=for-the-badge&logo=typescript&logoColor=white)
![TailwindCSS](https://img.shields.io/badge/Tailwind_CSS-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)
![NodeJS](https://img.shields.io/badge/Node.js-339933?style=for-the-badge&logo=nodedotjs&logoColor=white)

## 📖 Sobre o Projeto

Interface moderna e responsiva desenvolvida para o **Sipel Logistics Helper**. O frontend consome a API GeoRoute para permitir que usuários consultem dados de clientes e acessem rotas de geolocalização de forma intuitiva e rápida.

Desenvolvido com **Angular 21** e **Standalone Components**, garantindo performance e modularidade.

## 🚀 Tecnologias Utilizadas

- **Angular 21**: Framework frontend principal.
- **Standalone Components**: Arquitetura modular sem NgModules.
- **TailwindCSS**: Framework de utilitários CSS para estilização rápida e responsiva.
- **TypeScript**: Tipagem estática para maior segurança no código.
- **Vercel Analytics**: Monitoramento de uso (integrado).

## ✨ Funcionalidades

- **🔐 Autenticação**: Login seguro para acesso a funcionalidades administrativas.
- **🔍 Busca Unificada**: Pesquisa inteligente que detecta automaticamente:
  - Número de Instalação
  - Conta Contrato
  - Número de Série
  - Número do Poste
- **🗺️ Integração com Mapas**: Visualização de dados e link direto para rotas no Google Maps.
- **📄 Importação de Dados**: Interface para upload de arquivos CSV para atualização da base (Admin).
- **📱 Responsividade**: Layout adaptável para desktops e dispositivos móveis.

## 🛠️ Instalação e Execução

### Pré-requisitos
- **Node.js** (LTS recomendado).
- **NPM** (Gerenciador de pacotes).
- **Angular CLI** (Globalmente ou via npx).

### Passo a Passo

1. **Acesse o diretório**
   ```bash
   cd frontend
   ```

2. **Instale as dependências**
   ```bash
   npm install
   ```

3. **Execute o servidor de desenvolvimento**
   ```bash
   ng serve
   ```
   Ou via npm:
   ```bash
   npm start
   ```

   A aplicação estará disponível em `http://localhost:4200`.

## 📂 Estrutura do Projeto

```text
src/app/
├── components/          # Componentes reutilizáveis (Header, Footer)
├── core/                # Serviços, Guards, Interceptors e Interfaces
├── features/            # Módulos de funcionalidade (Login, Search, Admin)
│   ├── admin/           # Importação e Cadastro
│   ├── auth/            # Página de Login
│   ├── client-details/  # Exibição de dados do cliente
│   └── search/          # Página de busca principal
└── environments/        # Configurações de ambiente (Dev/Prod)
```

## 🤝 Contribuição

Siga o padrão de branches e commits estabelecido no repositório principal.

---
<p align="center">
  Desenvolvido para <strong>Sipel Construções LTDA</strong>
</p>