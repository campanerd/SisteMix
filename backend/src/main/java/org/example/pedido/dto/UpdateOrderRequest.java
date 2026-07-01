package org.example.pedido.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateOrderRequest(
        @NotNull Long id,
        LocalDate dataEmissao,
        LocalDate dataPedido,
        @Positive BigDecimal valorTotal,
        String observacao,
        Long idVendedor
) {}
