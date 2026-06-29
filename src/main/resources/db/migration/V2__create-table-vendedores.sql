CREATE TABLE vendedores (
    id       BIGSERIAL    PRIMARY KEY,
    nome     VARCHAR(255) NOT NULL,
    cpf      VARCHAR(14)  UNIQUE,
    telefone VARCHAR(20),
    ativo    BOOLEAN      NOT NULL DEFAULT TRUE
);
