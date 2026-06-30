# SisteMix — Contexto do Projeto

## O que é este projeto

Sistema web de **acompanhamento e consulta de boletos** (pedidos parcelados). Desenvolvido para uma empresa que precisa controlar quais boletos foram pagos ou não, acessível por múltiplas máquinas (aproximadamente 3) na rede da empresa.

O sistema **não emite boletos** — apenas registra e acompanha os que já foram gerados externamente.

## Stack

| Camada   | Tecnologia                                                              |
|----------|-------------------------------------------------------------------------|
| Frontend | React 19 + TypeScript + Vite + MUI + React Router + TanStack Query      |
| Backend  | Java 26 + Spring Boot 4.0.5 + Maven                                      |
| Banco    | PostgreSQL (Docker, porta 4747)                                          |

## Decisões de produto

- Status das parcelas (`PAGO`, `PENDENTE`, `EM_ATRASO`) é alterado **manualmente** pelo usuário
- Número do pedido é **digitado pelo usuário** (não gerado automaticamente)
- Sistema para **uma empresa apenas** (sem multitenancy)
- **Sem autenticação** por enquanto — sistema de usuário único; login planejado para versão futura
- Sem desconto ou multa nas parcelas — pelo menos por ora
- Cliente **não pode ser trocado** em um pedido após criação (apenas vendedor pode ser alterado)

## Configuração do ambiente

### Java
- **Versão**: Java 26
- **Path**: `C:/Users/davi.fernandes/.jdks/openjdk-26`
- **Maven**: usar Maven bundled do IntelliJ:
  ```
  "C:/Program Files/JetBrains/IntelliJ IDEA 2026.1/plugins/maven/lib/maven3/bin/mvn.cmd"
  ```
  com variável de ambiente `JAVA_HOME="C:/Users/davi.fernandes/.jdks/openjdk-26"`

### PostgreSQL (Docker)
O banco roda em Docker na porta **4747** para evitar conflito com o PostgreSQL local do Windows (porta 5432).

```bash
# Subir o container (Ubuntu/WSL):
docker run --name sistemix-db \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=sistemix \
  -p 4747:5432 \
  -d postgres
```

### application.properties
```properties
spring.datasource.url=jdbc:postgresql://localhost:4747/sistemix
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
server.port=8080
```

## Arquitetura do backend

Estrutura de pacotes **por domínio**, seguindo o padrão do projeto de referência (VollMed/Alura em `C:\Users\davi.fernandes\Documents\Estudo\api`):

```
src/main/java/org/example/
├── SisteMixApplication.java
├── cliente/
│   ├── Cliente.java
│   ├── ClienteRepository.java
│   ├── DadosCadastroCliente.java
│   ├── DadosAtualizacaoCliente.java
│   ├── DadosListagemCliente.java
│   └── DadosDetalhamentoCliente.java
├── vendedor/
│   ├── Vendedor.java
│   ├── VendedorRepository.java
│   ├── DadosCadastroVendedor.java
│   ├── DadosAtualizacaoVendedor.java
│   ├── DadosListagemVendedor.java
│   └── DadosDetalhamentoVendedor.java
├── pedido/
│   ├── Pedido.java
│   ├── PedidoRepository.java
│   ├── PedidoService.java          ← service existe por causa da geração automática de parcelas
│   ├── DadosCadastroPedido.java
│   ├── DadosAtualizacaoPedido.java
│   ├── DadosListagemPedido.java
│   └── DadosDetalhamentoPedido.java
├── parcela/
│   ├── Parcela.java
│   ├── ParcelaRepository.java
│   ├── StatusParcela.java
│   ├── DadosAtualizacaoStatusParcela.java
│   ├── DadosListagemParcela.java
│   └── DadosDetalhamentoParcela.java
└── controller/
    ├── ClienteController.java
    ├── VendedorController.java
    ├── PedidoController.java
    └── ParcelaController.java
```

### Convenções de código

- **DTOs**: Java Records com prefixo `Dados*` → `DadosCadastro*`, `DadosAtualizacao*`, `DadosListagem*`, `DadosDetalhamento*`
- **Lombok**: `@Getter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@EqualsAndHashCode(of = "id")`
- **Soft delete**: campo `ativo` (Boolean) + método `excluir()` em Cliente, Vendedor e Pedido
- **Lazy fetching**: `@ManyToOne(fetch = FetchType.LAZY)` nos relacionamentos de Pedido → Cliente e Pedido → Vendedor
- **Service layer**: criado apenas quando há lógica de negócio (ex: `PedidoService` para gerar parcelas automaticamente)
- **Paginação**: `@PageableDefault(size = 10, sort = {"campo"})` nos endpoints de listagem

## Domínios implementados

### Cliente
- CRUD completo com soft delete
- Campos: `nome`, `telefone`, `cpfCnpj`, `email`, `ativo`
- Atualização null-safe (só altera campos enviados)

### Vendedor
- CRUD completo com soft delete
- Campos: `nome`, `cpf`, `telefone`, `ativo`

### Pedido
- CRUD completo com soft delete
- Campos: `numeroPedido`, `dataEmissao`, `dataPedido`, `valorTotal`, `totalParcelas`, `observacao`, `ativo`
- Relacionamentos LAZY com Cliente e Vendedor
- `idCliente` não pode ser alterado após criação; `idVendedor` pode
- Ao criar um pedido, as parcelas são **geradas automaticamente** pelo `PedidoService`

### Parcela
- Gerada automaticamente ao criar pedido (nunca criada manualmente)
- Vencimento: `dataPedido + N meses` (1 mês para parcela 1, 2 meses para parcela 2, etc.)
- Valor: divisão com `RoundingMode.DOWN`; a **última parcela absorve o arredondamento**
- Status inicial: sempre `PENDENTE`
- `dataPagamento`: preenchida automaticamente quando status muda para `PAGO`; limpa nos outros status
- Endpoints: GET /parcelas, GET /parcelas/{id}, GET /parcelas/pedido/{idPedido}, PATCH /parcelas/{id}/status

## Regra de geração de parcelas

```java
// PedidoService.gerarParcelas()
BigDecimal valorParcela = valorTotal.divide(BigDecimal.valueOf(total), 2, RoundingMode.DOWN);
BigDecimal valorUltima  = valorTotal.subtract(valorParcela.multiply(BigDecimal.valueOf(total - 1)));
// Parcelas 1..N-1 = valorParcela | Parcela N = valorUltima
// Vencimento da parcela i = dataPedido.plusMonths(i)
```

## Migrações Flyway

```
src/main/resources/db/migration/
├── V1__create-table-clientes.sql
├── V2__create-table-vendedores.sql
├── V3__create-table-pedidos.sql      ← FK para clientes e vendedores
└── V4__create-table-parcelas.sql     ← FK para pedidos
```

## Dependências principais (pom.xml)

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.5</version>
</parent>
<properties>
    <java.version>26</java.version>
</properties>

<!-- spring-boot-starter-webmvc -->
<!-- spring-boot-starter-data-jpa -->
<!-- spring-boot-starter-validation -->
<!-- spring-boot-starter-flyway -->
<!-- flyway-database-postgresql  ← necessário separadamente no Spring Boot 4 -->
<!-- postgresql (runtime) -->
<!-- lombok -->
<!-- spring-boot-starter-webmvc-test (test) -->
```

> **Atenção**: `flyway-database-postgresql` deve ser declarado explicitamente — o starter do Spring Boot 4 não o inclui automaticamente.

## Testes com Bruno API Client

Arquivos YAML em `docs/bruno/` (20 arquivos no total). Variáveis de ambiente usadas:

| Variável         | Uso                           |
|------------------|-------------------------------|
| `{{IdCliente}}`  | ID de cliente nas URLs/body   |
| `{{IdVendedor}}` | ID de vendedor nas URLs/body  |
| `{{IdPedido}}`   | ID de pedido nas URLs/body    |
| `{{IdParcela}}`  | ID de parcela nas URLs/body   |

Formato de body nos arquivos Bruno:
```yaml
body:
  type: json
  data: |-
    {
      "campo": "valor"
    }
```

## Filtros disponíveis em GET /parcelas

- `status` (PAGO / PENDENTE / EM_ATRASO)
- `vencimentoInicio` (LocalDate)
- `vencimentoFim` (LocalDate)
- `valorMin` (BigDecimal)
- `valorMax` (BigDecimal)

## Frontend (`frontend/`)

Projeto React criado com Vite + TypeScript. Roda em `http://localhost:5173`.

### Stack do front
- **Vite** — build/dev server
- **MUI (Material UI)** — biblioteca de componentes; tabela via `@mui/x-data-grid`, date picker via `@mui/x-date-pickers`
- **React Router** — navegação entre telas
- **TanStack Query** — busca/cache de dados da API (`useQuery`/`useMutation`)
- **Axios** — cliente HTTP
- **dayjs** — manipulação de datas (locale pt-br)

### Estrutura de pastas
```
frontend/src/
├── api/          → conexão com o backend (client.ts = axios; um arquivo por domínio)
├── types/        → interfaces TypeScript espelhando os DTOs do backend
├── theme/        → tema do MUI
├── utils/        → formatação (moeda, data, status)
├── components/   → componentes reutilizáveis (Layout = menu lateral + app bar)
├── pages/        → telas
├── App.tsx       → providers (Query, Theme, Localization) + rotas
└── main.tsx
```

### Convenções do front
- URL da API vem de `.env` (`VITE_API_URL`, padrão `http://localhost:8080`)
- Datas trafegam como ISO `AAAA-MM-DD` (compatível com `LocalDate` do back); formatação para `DD/MM/AAAA` só na exibição
- `BigDecimal` do back chega como `number` no JSON
- Listagens paginadas chegam como `Page<T>` (Spring); `/parcelas` retorna lista simples (não paginada)

### Como rodar o front
```bash
cd frontend
npm install   # só na primeira vez
npm run dev
```
> O backend precisa estar rodando (com o Docker do banco no ar) para a API responder.

### Telas do front
- ✅ **Acompanhamento de Parcelas** (`/`) — filtros (status, vencimento, valor) + DataGrid + mudança de status via menu de ações
- ⏳ Pedidos, Clientes, Vendedores, Dashboard — placeholders ("em construção")

## CORS

Configurado em `org.example.config.CorsConfiguration`, liberando origem `http://localhost:5173` (Vite) para todos os métodos. **Após mexer no CORS, reinicie o backend.**

## Telas planejadas (por prioridade)

1. **Acompanhamento de parcelas** — listagem com filtros por vencimento, valor e status
2. **Detalhe do pedido** — visualização de um pedido com todas as suas parcelas
3. **Dashboard** — resumo geral (total em carteira, pendente, em atraso, pago)
4. **Cadastro de Clientes**
5. **Cadastro de Vendedores**
6. **Cadastro de Pedidos**

## Testes (backend)

42 testes, todos passando. Testes unitários puros com **JUnit 5 + Mockito + AssertJ** (sem Spring context):
- Entidades: `ClienteTest`, `VendedorTest`, `PedidoTest`, `ParcelaTest`
- Service: `PedidoServiceTest` (geração de parcelas, arredondamento, vencimentos)
- Controllers: `*ControllerTest` (chamam os métodos direto e verificam `ResponseEntity`)

> **Nota Spring Boot 4**: `@WebMvcTest` foi descartado — a abordagem é testar o controller como classe comum com `@ExtendWith(MockitoExtension.class)`, `@Mock` no repository e `@InjectMocks` no controller. Validação de `@Valid` e serialização JSON não são cobertas por esses testes (ficam para o Bruno).

## O que ainda não foi feito

- [ ] Frontend: telas de Pedidos, Clientes, Vendedores e Dashboard (só Acompanhamento está pronta)
- [ ] Tratamento de erros global no back (`@ExceptionHandler` — hoje ID inexistente retorna 500 em vez de 404)
- [ ] Autenticação/login (tabela de usuario já discutida, adiada para versão futura)
