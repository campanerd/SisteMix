package org.example.installment.service;

import org.example.installment.dto.InstallmentResponse;
import org.example.installment.dto.InstallmentSummary;
import org.example.installment.dto.UpdateInstallmentStatusRequest;
import org.example.installment.enums.InstallmentStatus;
import org.example.installment.repository.InstallmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        return repository.findWithFilters(status, dueDateFrom, dueDateTo, amountMin, amountMax)
                .stream().map(InstallmentSummary::new).toList();
    }

    public InstallmentResponse findById(Long id) {
        return new InstallmentResponse(repository.getReferenceById(id));
    }

    public List<InstallmentSummary> listByOrder(Long orderId) {
        return repository.findByOrderId(orderId)
                .stream().map(InstallmentSummary::new).toList();
    }

    @Transactional
    public InstallmentResponse updateStatus(Long id, UpdateInstallmentStatusRequest data) {
        var installment = repository.getReferenceById(id);
        installment.updateStatus(data.status());
        return new InstallmentResponse(installment);
    }
}
