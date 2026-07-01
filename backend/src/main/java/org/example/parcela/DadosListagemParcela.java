package org.example.parcela;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosListagemParcela(
        Long id,
        Integer numeroParcela,
        Integer totalParcelas,
        BigDecimal valor,
        LocalDate vencimento,
        StatusParcela status,
        LocalDate dataPagamento,
        String numeroPedido,
        String nomeCliente
) {
    public DadosListagemParcela(Parcela parcela) {
        this(
                parcela.getId(),
                parcela.getNumeroParcela(),
                parcela.getPedido().getTotalParcelas(),
                parcela.getValor(),
                parcela.getVencimento(),
                parcela.getStatus(),
                parcela.getDataPagamento(),
                parcela.getPedido().getNumeroPedido(),
                parcela.getPedido().getClient().getNome()
        );
    }
}
