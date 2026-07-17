package org.siste.mix.installment.repository;

import org.siste.mix.installment.enums.InstallmentStatus;
import org.siste.mix.installment.model.Installment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface InstallmentRepository extends JpaRepository<Installment, Long>, JpaSpecificationExecutor<Installment> {

    @EntityGraph(attributePaths = {"order", "order.client"})
    @Override
    List<Installment> findAll(Specification<Installment> spec);

    boolean existsByOrderIdAndStatus(Long orderId, InstallmentStatus status);

    void deleteAllByOrderId(Long orderId);
}
