package org.example.parcela;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InstallmentSummary(
        Long id,
        Integer numeroParcela,
        Integer totalParcelas,
        BigDecimal valor,
        LocalDate vencimento,
        InstallmentStatus status,
        LocalDate dataPagamento,
        String numeroPedido,
        String nomeCliente
) {
    public InstallmentSummary(Parcela parcela) {
        this(
                parcela.getId(),
                parcela.getNumeroParcela(),
                parcela.getPedido().getTotalParcelas(),
                parcela.getValor(),
                parcela.getVencimento(),
                parcela.getStatus(),
                parcela.getDataPagamento(),
                parcela.getPedido().getNumeroPedido(),
                parcela.getPedido().getCliente().getNome()
        );
    }
}
