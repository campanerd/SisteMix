package org.example.pedido;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosAtualizacaoPedido(
        @NotNull Long id,
        LocalDate dataEmissao,
        LocalDate dataPedido,
        @Positive BigDecimal valorTotal,
        String observacao,
        Long idVendedor
) {}
