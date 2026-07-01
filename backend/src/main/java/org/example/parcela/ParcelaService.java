package org.example.parcela;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ParcelaService {

    @Autowired
    private ParcelaRepository repository;

    public List<InstallmentSummary> list(InstallmentStatus status, LocalDate vencimentoInicio, LocalDate vencimentoFim, BigDecimal valorMin, BigDecimal valorMax) {
        return repository.findWithFilters(status, vencimentoInicio, vencimentoFim, valorMin, valorMax)
                .stream().map(InstallmentSummary::new).toList();
    }

    public InstallmentResponse findById(Long id) {
        return new InstallmentResponse(repository.getReferenceById(id));
    }

    public List<InstallmentSummary> listByOrder(Long orderId) {
        return repository.findByPedidoId(orderId)
                .stream().map(InstallmentSummary::new).toList();
    }

    @Transactional
    public InstallmentResponse updateStatus(Long id, UpdateInstallmentStatusRequest data) {
        var parcela = repository.getReferenceById(id);
        parcela.updateStatus(data.status());
        return new InstallmentResponse(parcela);
    }
}
