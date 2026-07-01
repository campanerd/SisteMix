package org.example.pedido;

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
                pedido.getCliente().getNome(),
                pedido.getVendedor().getNome(),
                pedido.getValorTotal(),
                pedido.getTotalParcelas(),
                pedido.getDataPedido()
        );
    }
}
