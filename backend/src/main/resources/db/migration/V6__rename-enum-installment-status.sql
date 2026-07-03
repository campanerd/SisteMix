UPDATE installments SET status = 'PAID'    WHERE status = 'PAGO';
UPDATE installments SET status = 'PENDING' WHERE status = 'PENDENTE';
UPDATE installments SET status = 'OVERDUE' WHERE status = 'EM_ATRASO';