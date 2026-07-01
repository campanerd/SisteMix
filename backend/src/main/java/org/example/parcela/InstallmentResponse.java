package org.example.parcela;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InstallmentResponse(
        Long id,
        Integer numeroParcela,
        Integer totalParcelas,
        BigDecimal valor,
        LocalDate vencimento,
        InstallmentStatus status,
        LocalDate dataPagamento,
        Long idPedido,
        String numeroPedido,
        String nomeCliente,
        String nomeVendedor
) {
    public InstallmentResponse(Parcela parcela) {
        this(
                parcela.getId(),
                parcela.getNumeroParcela(),
                parcela.getPedido().getTotalParcelas(),
                parcela.getValor(),
                parcela.getVencimento(),
                parcela.getStatus(),
                parcela.getDataPagamento(),
                parcela.getPedido().getId(),
                parcela.getPedido().getNumeroPedido(),
                parcela.getPedido().getCliente().getNome(),
                parcela.getPedido().getVendedor().getNome()
        );
    }
}
