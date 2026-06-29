CREATE TABLE clientes (
    id       BIGSERIAL    PRIMARY KEY,
    nome     VARCHAR(255) NOT NULL,
    telefone VARCHAR(20),
    cpf_cnpj VARCHAR(18)  UNIQUE,
    email    VARCHAR(255),
    ativo    BOOLEAN      NOT NULL DEFAULT TRUE
);
