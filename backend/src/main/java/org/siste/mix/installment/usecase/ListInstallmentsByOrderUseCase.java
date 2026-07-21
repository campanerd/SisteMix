package org.siste.mix.installment.usecase;

import org.siste.mix.installment.dto.InstallmentSummary;
import org.siste.mix.installment.repository.InstallmentRepository;
import org.siste.mix.installment.repository.InstallmentSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListInstallmentsByOrderUseCase {

    @Autowired
    private InstallmentRepository repository;

    public List<InstallmentSummary> listByOrder(Long orderId) {
        var spec = Specification
                .where(InstallmentSpec.forOrder(orderId))
                .and(InstallmentSpec.orderIsActive());
        var sort = Sort.by("installmentNumber");
        return repository.findAll(spec, sort).stream().map(InstallmentSummary::new).toList();
    }
}
