package org.siste.mix.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateOrderRequest(
        @NotNull Long id,
        LocalDate issueDate,
        LocalDate orderDate,
        @Positive BigDecimal totalAmount,
        String notes,
        Long sellerId
) {}
