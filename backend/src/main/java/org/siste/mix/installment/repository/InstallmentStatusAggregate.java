package org.siste.mix.installment.repository;

import org.siste.mix.installment.enums.InstallmentStatus;

import java.math.BigDecimal;

public interface InstallmentStatusAggregate {
    InstallmentStatus getStatus();
    Long getCount();
    BigDecimal getTotalAmount();
}
