package org.example.order.dto;

import org.example.order.model.Order;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderResponse(
        Long id,
        String orderNumber,
        LocalDate issueDate,
        LocalDate orderDate,
        BigDecimal totalAmount,
        Integer totalInstallments,
        String notes,
        Long clientId,
        String clientName,
        Long sellerId,
        String sellerName
) {
    public OrderResponse(Order order) {
        this(
                order.getId(),
                order.getOrderNumber(),
                order.getIssueDate(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getTotalInstallments(),
                order.getNotes(),
                order.getClient().getId(),
                order.getClient().getName(),
                order.getSeller().getId(),
                order.getSeller().getName()
        );
    }
}
