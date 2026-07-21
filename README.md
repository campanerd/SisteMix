# SisteMix

<p align="center">
  <img src="https://img.shields.io/badge/Java-26-blue?logo=openjdk" alt="Java 26"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0-brightgreen?logo=springboot" alt="Spring Boot 4"/>
  <img src="https://img.shields.io/badge/PostgreSQL-Docker-4169E1?logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/React-19-61DAFB?logo=react&logoColor=black" alt="React 19"/>
  <img src="https://img.shields.io/badge/TypeScript-5-3178C6?logo=typescript&logoColor=white" alt="TypeScript"/>
<img src="https://github.com/campanerd/SisteMix/actions/workflows/ci.yml/badge.svg" alt="CI"/>  
<img src="https://img.shields.io/badge/status-em%20desenvolvimento-yellow" alt="Status"/>
</p>

> Sistema web para **acompanhamento e consulta de boletos parcelados**. Permite que múltiplas máquinas na rede de uma empresa consultem e atualizem o status de parcelas em tempo real. O sistema não emite boletos, registra e acompanha os que já foram gerados externamente.

---

## Sumário

- [Funcionalidades](#funcionalidades)
- [Tecnologias](#tecnologias)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Modelagem](#modelagem)
- [Como executar](#como-executar)
- [Documentação da API](#documentação-da-api)

---

## Funcionalidades

| Status | Funcionalidade |
|--------|----------------|
| ✅ | Autenticação via JWT com controle de acesso por perfil (`USER`, `ADMIN`, `DEV`) |
| ✅ | Listagem de parcelas com filtros por status, vencimento e valor |
| ✅ | Próxima parcela não paga de cada pedido em destaque quando nenhum filtro é aplicado |
| ✅ | Atualização manual de status (Pago, Pendente, Em atraso) |
| ✅ | Geração automática de parcelas ao cadastrar um pedido |
| ✅ | Divisão de valor com arredondamento aplicado à última parcela |
| ✅ | Sincronização de parcelas ao editar valor/data do pedido, bloqueando a alteração se já houver parcela paga |
| ✅ | Auditoria de pedidos: quem criou, quem editou (com o diff dos campos) e quem excluiu |
| ✅ | CRUD completo de Clientes, Vendedores, Pedidos e Usuários via API REST |
| 🔜 | Cadastro de Pedidos, Clientes e Vendedores via interface web |
| 🔜 | Dashboard com resumo financeiro (total em carteira, pago, em atraso) |

---

## Tecnologias

### Backend

| Tecnologia | Versão | Uso |
|------------|--------|-----|
| Java | 26 | Linguagem principal |
| Spring Boot | 4.0 | Framework web |
| Spring Data JPA | — | Persistência |
| Spring Security | — | Autenticação e autorização |
| java-jwt (Auth0) | — | Geração e validação de tokens JWT |
| PostgreSQL | — | Banco de dados |
| Flyway | — | Migrations do banco |
| Bean Validation | — | Validação de dados |
| Lombok | — | Redução de boilerplate |
| springdoc-openapi | — | Documentação interativa (Swagger UI) |

### Frontend

| Tecnologia | Uso |
|------------|-----|
| React 19 + TypeScript | Interface e tipagem |
| Vite | Build e dev server |
| Material UI (MUI) | Componentes e design system |
| MUI X Data Grid | Tabela de parcelas |
| TanStack Query | Busca e cache de dados da API |
| Axios | Cliente HTTP, com interceptors de JWT e tratamento de 401 |
| dayjs | Manipulação de datas |
| React Router | Navegação entre telas e rota protegida por login |

---

## Estrutura do projeto

```
SisteMix/
├── backend/
│   ├── src/main/java/org/siste/mix/
│   │   ├── client/             # Entidade, repositório, service, controller e DTOs
│   │   ├── seller/
│   │   ├── order/               # inclui histórico de alterações (auditoria)
│   │   ├── installment/
│   │   ├── user/                # cadastro de usuários
│   │   ├── infra/
│   │   │   ├── security/        # JWT, filtro de autenticação, SecurityConfig
│   │   │   └── exception/       # tratamento global de erros
│   │   └── config/               # CORS, Swagger, prefixo /api
│   ├── src/main/resources/
│   │   └── db/migration/       # Scripts Flyway (V1–V10)
│   ├── docs/
│   │   ├── bruno/               # Coleção de requisições (Bruno API client)
│   │   └── DER/                 # Diagrama e schema do banco
│   └── pom.xml
└── frontend/
    └── src/
        ├── api/                # Cliente Axios + um arquivo por domínio (auth, installments...)
        ├── components/         # Componentes reutilizáveis (Layout)
        ├── pages/              # Telas da aplicação (Login, Acompanhamento de Parcelas...)
        ├── types/              # Interfaces TypeScript espelhando os DTOs do backend
        ├── utils/              # Formatação e filtros padrão
        └── theme/              # Tema MUI
```

---

## Modelagem

<p align="center">
  <img src="backend/docs/DER/SisteMix.png" alt="Diagrama entidade-relacionamento" width="700"/>
</p>

---

## Como executar

### Pré-requisitos

- Docker
- Java 26
- Node.js 20+

### 1. Banco de dados

```bash
docker run --name sistemix-db \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=sistemix \
  -p 4747:5432 \
  -d postgres
```

### 2. Backend

Crie um arquivo `.env` na raiz do projeto com as variáveis abaixo (o backend lê essas variáveis de ambiente na inicialização):

```
DB_URL=jdbc:postgresql://localhost:4747/sistemix
DB_USER=postgres
DB_PASSWORD=postgres
JWT_SECRET=uma_frase_longa_e_aleatoria
```

```bash
cd backend
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

A aplicação estará disponível em `http://localhost:5173`. Como o sistema exige login, é necessário criar um usuário antes (via `POST /api/users`, endpoint público) para conseguir acessar.

---

## Documentação da API

A API completa está documentada via Swagger:

`http://localhost:8080/swagger-ui/index.html`

Domínios disponíveis: **Autenticação**, **Usuários**, **Clientes**, **Vendedores**, **Pedidos** (incluindo histórico de alterações) e **Parcelas**.

Também há uma coleção pronta para o [Bruno API Client](https://www.usebruno.com/) em `backend/docs/bruno/`.
