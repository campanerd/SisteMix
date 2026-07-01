package org.example.pedido.dto;

import org.example.pedido.model.Pedido;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderSummary(
        Long id,
        String numeroPedido,
        String nomeCliente,
        String nomeVendedor,
        BigDecimal valorTotal,
        Integer totalParcelas,
        LocalDate dataPedido
) {
    public OrderSummary(Pedido pedido) {
        this(
                pedido.getId(),
                pedido.getNumeroPedido(),
                pedido.getClient().getNome(),
                pedido.getSeller().getNome(),
                pedido.getValorTotal(),
                pedido.getTotalParcelas(),
                pedido.getDataPedido()
        );
    }
}
