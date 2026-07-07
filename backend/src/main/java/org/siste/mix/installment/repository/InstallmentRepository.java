package org.siste.mix.installment.repository;

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
}
