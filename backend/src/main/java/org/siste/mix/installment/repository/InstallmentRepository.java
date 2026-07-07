package org.siste.mix.installment.repository;

import org.siste.mix.installment.model.Installment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InstallmentRepository extends JpaRepository<Installment, Long>, JpaSpecificationExecutor<Installment> {
}
