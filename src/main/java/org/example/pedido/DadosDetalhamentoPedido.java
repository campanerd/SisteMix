package org.example.pedido;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosDetalhamentoPedido(
        Long id,
        String numeroPedido,
        LocalDate dataEmissao,
        LocalDate dataPedido,
        BigDecimal valorTotal,
        Integer totalParcelas,
        String observacao,
        Long idCliente,
        String nomeCliente,
        Long idVendedor,
        String nomeVendedor
) {
    public DadosDetalhamentoPedido(Pedido pedido) {
        this(
                pedido.getId(),
                pedido.getNumeroPedido(),
                pedido.getDataEmissao(),
                pedido.getDataPedido(),
                pedido.getValorTotal(),
                pedido.getTotalParcelas(),
                pedido.getObservacao(),
                pedido.getCliente().getId(),
                pedido.getCliente().getNome(),
                pedido.getVendedor().getId(),
                pedido.getVendedor().getNome()
        );
    }
}
