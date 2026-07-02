package org.siste.mix.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateOrderRequest(
        @NotBlank String orderNumber,
        @NotNull LocalDate issueDate,
        @NotNull LocalDate orderDate,
        @NotNull @Positive BigDecimal totalAmount,
        @NotNull @Positive Integer totalInstallments,
        String notes,
        @NotNull Long clientId,
        @NotNull Long sellerId
) {}
