package org.siste.mix.order.dto;

import org.siste.mix.order.model.Order;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderSummary(
        Long id,
        String orderNumber,
        String clientName,
        String sellerName,
        BigDecimal totalAmount,
        Integer totalInstallments,
        LocalDate orderDate
) {
    public OrderSummary(Order order) {
        this(
                order.getId(),
                order.getOrderNumber(),
                order.getClient().getName(),
                order.getSeller().getName(),
                order.getTotalAmount(),
                order.getTotalInstallments(),
                order.getOrderDate()
        );
    }
}
