package org.siste.mix.installment.usecase;

import org.siste.mix.installment.dto.InstallmentSummary;
import org.siste.mix.installment.enums.InstallmentStatus;
import org.siste.mix.installment.repository.InstallmentRepository;
import org.siste.mix.installment.repository.InstallmentSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ListInstallmentsUseCase {

    @Autowired
    private InstallmentRepository repository;

    public List<InstallmentSummary> list(InstallmentStatus status, LocalDate dueDateFrom, LocalDate dueDateTo, BigDecimal amountMin, BigDecimal amountMax, boolean showAll) {
        var noFiltersApplied = !showAll && status == null && dueDateFrom == null && dueDateTo == null && amountMin == null && amountMax == null;
        if (noFiltersApplied) {
            return listNextDuePerOrder();
        }

        var spec = Specification
                .where(InstallmentSpec.orderIsActive())
                .and(InstallmentSpec.hasStatus(status))
                .and(InstallmentSpec.dueDateFrom(dueDateFrom))
                .and(InstallmentSpec.dueDateTo(dueDateTo))
                .and(InstallmentSpec.amountMin(amountMin))
                .and(InstallmentSpec.amountMax(amountMax));
        return repository.findAll(spec).stream().map(InstallmentSummary::new).toList();
    }

    private List<InstallmentSummary> listNextDuePerOrder() {
        var ids = repository.findNextUnpaidInstallmentIdsPerOrder();
        if (ids.isEmpty()) {
            return List.of();
        }
        var spec = Specification.where(InstallmentSpec.idIn(ids));
        return repository.findAll(spec).stream().map(InstallmentSummary::new).toList();
    }
}
