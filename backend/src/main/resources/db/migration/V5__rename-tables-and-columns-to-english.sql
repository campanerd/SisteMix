-- clients
ALTER TABLE clientes RENAME TO clients;
ALTER TABLE clients RENAME COLUMN nome     TO name;
ALTER TABLE clients RENAME COLUMN telefone TO phone;
ALTER TABLE clients RENAME COLUMN ativo    TO active;

-- sellers
ALTER TABLE vendedores RENAME TO sellers;
ALTER TABLE sellers RENAME COLUMN nome     TO name;
ALTER TABLE sellers RENAME COLUMN telefone TO phone;
ALTER TABLE sellers RENAME COLUMN ativo    TO active;

-- orders
ALTER TABLE pedidos RENAME TO orders;
ALTER TABLE orders RENAME COLUMN numero_pedido  TO order_number;
ALTER TABLE orders RENAME COLUMN data_emissao   TO issue_date;
ALTER TABLE orders RENAME COLUMN data_pedido    TO order_date;
ALTER TABLE orders RENAME COLUMN valor_total    TO total_amount;
ALTER TABLE orders RENAME COLUMN total_parcelas TO total_installments;
ALTER TABLE orders RENAME COLUMN observacao     TO notes;
ALTER TABLE orders RENAME COLUMN id_cliente     TO client_id;
ALTER TABLE orders RENAME COLUMN id_vendedor    TO seller_id;
ALTER TABLE orders RENAME COLUMN ativo          TO active;

-- installments
ALTER TABLE parcelas RENAME TO installments;
ALTER TABLE installments RENAME COLUMN numero_parcela TO installment_number;
ALTER TABLE installments RENAME COLUMN valor          TO amount;
ALTER TABLE installments RENAME COLUMN vencimento     TO due_date;
ALTER TABLE installments RENAME COLUMN data_pagamento TO payment_date;
ALTER TABLE installments RENAME COLUMN id_pedido      TO order_id;
