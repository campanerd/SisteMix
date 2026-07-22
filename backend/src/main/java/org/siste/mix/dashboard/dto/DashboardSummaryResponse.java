package org.siste.mix.dashboard.dto;

import java.math.BigDecimal;

public record DashboardSummaryResponse(
        long activeOrdersCount,
        long paidCount,
        long pendingCount,
        long overdueCount,
        BigDecimal totalReceivedAmount,
        BigDecimal totalToReceiveAmount
) {
}
