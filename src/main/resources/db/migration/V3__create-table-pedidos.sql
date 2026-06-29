CREATE TABLE pedidos (
    id             BIGSERIAL     PRIMARY KEY,
    numero_pedido  VARCHAR(50)   NOT NULL UNIQUE,
    data_emissao   DATE          NOT NULL,
    data_pedido    DATE          NOT NULL,
    valor_total    NUMERIC(15,2) NOT NULL,
    total_parcelas INTEGER       NOT NULL,
    observacao     TEXT,
    id_cliente     BIGINT        NOT NULL REFERENCES clientes(id),
    id_vendedor    BIGINT        NOT NULL REFERENCES vendedores(id),
    ativo          BOOLEAN       NOT NULL DEFAULT TRUE
);
