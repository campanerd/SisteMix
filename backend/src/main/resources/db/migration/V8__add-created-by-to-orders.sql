ALTER TABLE orders
    ADD COLUMN created_by BIGINT REFERENCES users(id);

UPDATE orders
SET created_by = (SELECT id FROM users WHERE email = 'system@sistemix.internal');

ALTER TABLE orders
    ALTER COLUMN created_by SET NOT NULL;
