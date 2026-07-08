CREATE TABLE order_history (
    id         BIGSERIAL    PRIMARY KEY,
    order_id   BIGINT       NOT NULL REFERENCES orders(id),
    user_id    BIGINT       NOT NULL REFERENCES users(id),
    action     VARCHAR(20)  NOT NULL,
    changed_at TIMESTAMP    NOT NULL DEFAULT now(),
    changes    JSONB
);
