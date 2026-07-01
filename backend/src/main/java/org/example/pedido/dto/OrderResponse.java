package org.example.pedido.dto;

import org.example.pedido.model.Pedido;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderResponse(
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
    public OrderResponse(Pedido pedido) {
        this(
                pedido.getId(),
                pedido.getNumeroPedido(),
                pedido.getDataEmissao(),
                pedido.getDataPedido(),
                pedido.getValorTotal(),
                pedido.getTotalParcelas(),
                pedido.getObservacao(),
                pedido.getClient().getId(),
                pedido.getClient().getNome(),
                pedido.getSeller().getId(),
                pedido.getSeller().getNome()
        );
    }
}
