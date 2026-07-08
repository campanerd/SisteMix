CREATE TABLE users (
    id       BIGSERIAL    PRIMARY KEY,
    name     VARCHAR(100) NOT NULL,
    email    VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(20)  NOT NULL,
    active   BOOLEAN      NOT NULL DEFAULT true
);

INSERT INTO users (name, email, password, role, active)
VALUES ('System', 'system@sistemix.internal', 'N/A', 'DEV', false);
