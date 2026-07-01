package org.example.parcela.dto;

import org.example.parcela.model.Parcela;
import org.example.parcela.enums.StatusParcela;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosDetalhamentoParcela(
        Long id,
        Integer numeroParcela,
        Integer totalParcelas,
        BigDecimal valor,
        LocalDate vencimento,
        StatusParcela status,
        LocalDate dataPagamento,
        Long idPedido,
        String numeroPedido,
        String nomeCliente,
        String nomeVendedor
) {
    public DadosDetalhamentoParcela(Parcela parcela) {
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
                parcela.getPedido().getClient().getNome(),
                parcela.getPedido().getSeller().getNome()
        );
    }
}
