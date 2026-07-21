package org.siste.mix.installment.service;

import jakarta.persistence.EntityNotFoundException;
import org.siste.mix.installment.dto.InstallmentResponse;
import org.siste.mix.installment.dto.InstallmentSummary;
import org.siste.mix.installment.dto.UpdateInstallmentStatusRequest;
import org.siste.mix.installment.enums.InstallmentStatus;
import org.siste.mix.installment.repository.InstallmentRepository;
import org.siste.mix.installment.repository.InstallmentSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class InstallmentService {

    @Autowired
    private InstallmentRepository repository;

    public List<InstallmentSummary> list(InstallmentStatus status, LocalDate dueDateFrom, LocalDate dueDateTo, BigDecimal amountMin, BigDecimal amountMax) {
        var noFiltersApplied = status == null && dueDateFrom == null && dueDateTo == null && amountMin == null && amountMax == null;
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

    public InstallmentResponse findById(Long id) {
        var installment = repository.findById(id)
                .filter(i -> Boolean.TRUE.equals(i.getOrder().getActive()))
                .orElseThrow(EntityNotFoundException::new);
        return new InstallmentResponse(installment);
    }

    public List<InstallmentSummary> listByOrder(Long orderId) {
        var spec = Specification
                .where(InstallmentSpec.forOrder(orderId))
                .and(InstallmentSpec.orderIsActive());
        return repository.findAll(spec).stream().map(InstallmentSummary::new).toList();
    }

    @Transactional
    public InstallmentResponse updateStatus(Long id, UpdateInstallmentStatusRequest data) {
        var installment = repository.getReferenceById(id);
        installment.updateStatus(data.status());
        return new InstallmentResponse(installment);
    }
}
