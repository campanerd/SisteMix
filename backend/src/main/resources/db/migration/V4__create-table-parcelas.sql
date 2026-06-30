CREATE TABLE parcelas (
    id              BIGSERIAL     PRIMARY KEY,
    numero_parcela  INTEGER       NOT NULL,
    valor           NUMERIC(15,2) NOT NULL,
    vencimento      DATE          NOT NULL,
    status          VARCHAR(20)   NOT NULL DEFAULT 'PENDENTE',
    data_pagamento  DATE,
    id_pedido       BIGINT        NOT NULL REFERENCES pedidos(id)
);
