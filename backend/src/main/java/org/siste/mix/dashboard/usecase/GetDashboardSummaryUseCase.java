package org.siste.mix.dashboard.usecase;

import org.siste.mix.dashboard.dto.DashboardSummaryResponse;
import org.siste.mix.installment.enums.InstallmentStatus;
import org.siste.mix.installment.repository.InstallmentRepository;
import org.siste.mix.installment.repository.InstallmentStatusAggregate;
import org.siste.mix.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GetDashboardSummaryUseCase {

    @Autowired
    private InstallmentRepository installmentRepository;

    @Autowired
    private OrderRepository orderRepository;

    public DashboardSummaryResponse get() {
        Map<InstallmentStatus, InstallmentStatusAggregate> byStatus = installmentRepository.aggregateActiveByStatus()
                .stream()
                .collect(Collectors.toMap(InstallmentStatusAggregate::getStatus, Function.identity()));

        long paidCount = countOf(byStatus, InstallmentStatus.PAID);
        long pendingCount = countOf(byStatus, InstallmentStatus.PENDING);
        long overdueCount = countOf(byStatus, InstallmentStatus.OVERDUE);

        BigDecimal totalReceivedAmount = amountOf(byStatus, InstallmentStatus.PAID);
        BigDecimal totalToReceiveAmount = amountOf(byStatus, InstallmentStatus.PENDING)
                .add(amountOf(byStatus, InstallmentStatus.OVERDUE));

        return new DashboardSummaryResponse(
                orderRepository.countByActiveTrue(),
                paidCount,
                pendingCount,
                overdueCount,
                totalReceivedAmount,
                totalToReceiveAmount
        );
    }

    private long countOf(Map<InstallmentStatus, InstallmentStatusAggregate> byStatus, InstallmentStatus status) {
        var aggregate = byStatus.get(status);
        return aggregate == null ? 0L : aggregate.getCount();
    }

    private BigDecimal amountOf(Map<InstallmentStatus, InstallmentStatusAggregate> byStatus, InstallmentStatus status) {
        var aggregate = byStatus.get(status);
        return aggregate == null ? BigDecimal.ZERO : aggregate.getTotalAmount();
    }
}
