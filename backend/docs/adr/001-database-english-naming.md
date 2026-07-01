# ADR-001 — Renomear tabelas e colunas do banco para inglês (snake_case)

**Status:** Aceito  
**Data:** 2026-07-01

---

## Contexto

O backend passou por uma reestruturação completa nas tasks 8 e 9: entidades, DTOs, repositórios e serviços foram renomeados para inglês e organizados em sub-pacotes por domínio. Porém, o schema do banco permaneceu em português, criando uma inconsistência entre a camada de código e a camada de persistência.

Estado atual das tabelas:

| Tabela | Colunas notáveis |
|---|---|
| `clientes` | `nome`, `telefone`, `cpf_cnpj`, `email`, `ativo` |
| `vendedores` | `nome`, `cpf`, `telefone`, `ativo` |
| `pedidos` | `numero_pedido`, `data_emissao`, `data_pedido`, `valor_total`, `total_parcelas`, `observacao`, `id_cliente`, `id_vendedor`, `ativo` |
| `parcelas` | `numero_parcela`, `valor`, `vencimento`, `status`, `data_pagamento`, `id_pedido` |

---

## Decisão

Renomear todas as tabelas e colunas para inglês em snake_case, mantendo consistência com o restante da codebase.

Mapeamento adotado:

| Antes | Depois |
|---|---|
| `clientes` | `clients` |
| `vendedores` | `sellers` |
| `pedidos` | `orders` |
| `parcelas` | `installments` |
| `nome` | `name` |
| `telefone` | `phone` |
| `cpf_cnpj` | `cpf_cnpj` *(mantido)* |
| `cpf` | `cpf` *(mantido)* |
| `ativo` | `active` |
| `numero_pedido` | `order_number` |
| `data_emissao` | `issue_date` |
| `data_pedido` | `order_date` |
| `valor_total` | `total_amount` |
| `total_parcelas` | `total_installments` |
| `observacao` | `notes` |
| `id_cliente` | `client_id` |
| `id_vendedor` | `seller_id` |
| `numero_parcela` | `installment_number` |
| `valor` | `amount` |
| `vencimento` | `due_date` |
| `data_pagamento` | `payment_date` |
| `id_pedido` | `order_id` |

A migration será feita via Flyway (`V5__rename-tables-and-columns-to-english.sql`) usando `ALTER TABLE ... RENAME` e `ALTER TABLE ... RENAME COLUMN`, sem perda de dados.

---

## Consequências

**Positivas:**
- Schema do banco alinhado com os nomes de entidades, DTOs e métodos do código
- Facilita leitura de queries e logs sem precisar "traduzir" mentalmente
- Consistência para quem entrar no projeto no futuro

**Negativas / Ações necessárias:**
- Anotações JPA precisam ser atualizadas: `@Table(name = ...)`, `@Column(name = ...)`, `@JoinColumn(name = ...)`
- Qualquer query nativa nos repositórios precisa ser atualizada
- Ambiente de produção/desenvolvimento com dados existentes precisa rodar a migration antes de subir o backend
