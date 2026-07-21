package org.siste.mix.installment.usecase;

import jakarta.persistence.EntityNotFoundException;
import org.siste.mix.installment.dto.InstallmentResponse;
import org.siste.mix.installment.repository.InstallmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindInstallmentByIdUseCase {

    @Autowired
    private InstallmentRepository repository;

    public InstallmentResponse findById(Long id) {
        var installment = repository.findById(id)
                .filter(i -> Boolean.TRUE.equals(i.getOrder().getActive()))
                .orElseThrow(EntityNotFoundException::new);
        return new InstallmentResponse(installment);
    }
}
