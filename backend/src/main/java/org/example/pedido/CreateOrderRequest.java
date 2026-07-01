package org.example.pedido;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateOrderRequest(
        @NotBlank String numeroPedido,
        @NotNull LocalDate dataEmissao,
        @NotNull LocalDate dataPedido,
        @NotNull @Positive BigDecimal valorTotal,
        @NotNull @Positive Integer totalParcelas,
        String observacao,
        @NotNull Long idCliente,
        @NotNull Long idVendedor
) {}
