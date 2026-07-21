package org.siste.mix.installment.usecase;

import org.siste.mix.installment.dto.InstallmentResponse;
import org.siste.mix.installment.dto.UpdateInstallmentStatusRequest;
import org.siste.mix.installment.repository.InstallmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateInstallmentStatusUseCase {

    @Autowired
    private InstallmentRepository repository;

    @Transactional
    public InstallmentResponse updateStatus(Long id, UpdateInstallmentStatusRequest data) {
        var installment = repository.getReferenceById(id);
        installment.updateStatus(data.status());
        return new InstallmentResponse(installment);
    }
}
