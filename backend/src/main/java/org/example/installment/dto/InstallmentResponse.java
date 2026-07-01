package org.example.installment.dto;

import org.example.installment.enums.InstallmentStatus;
import org.example.installment.model.Installment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InstallmentResponse(
        Long id,
        Integer installmentNumber,
        Integer totalInstallments,
        BigDecimal amount,
        LocalDate dueDate,
        InstallmentStatus status,
        LocalDate paymentDate,
        Long orderId,
        String orderNumber,
        String clientName,
        String sellerName
) {
    public InstallmentResponse(Installment installment) {
        this(
                installment.getId(),
                installment.getInstallmentNumber(),
                installment.getOrder().getTotalInstallments(),
                installment.getAmount(),
                installment.getDueDate(),
                installment.getStatus(),
                installment.getPaymentDate(),
                installment.getOrder().getId(),
                installment.getOrder().getOrderNumber(),
                installment.getOrder().getClient().getName(),
                installment.getOrder().getSeller().getName()
        );
    }
}
