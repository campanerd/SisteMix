package org.siste.mix.order.dto;

import org.siste.mix.order.enums.OrderHistoryAction;

import java.time.LocalDateTime;
import java.util.List;

public record OrderHistoryResponse(
        Long id,
        OrderHistoryAction action,
        LocalDateTime changedAt,
        String changedByName,
        List<FieldChange> changes
) {}
