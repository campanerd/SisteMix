package org.example.installment.repository;

import org.example.installment.enums.InstallmentStatus;
import org.example.installment.model.Installment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface InstallmentRepository extends JpaRepository<Installment, Long> {

    List<Installment> findByOrderId(Long orderId);

    @Query("SELECT i FROM Installment i WHERE " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:dueDateFrom IS NULL OR i.dueDate >= :dueDateFrom) AND " +
           "(:dueDateTo IS NULL OR i.dueDate <= :dueDateTo) AND " +
           "(:amountMin IS NULL OR i.amount >= :amountMin) AND " +
           "(:amountMax IS NULL OR i.amount <= :amountMax)")
    List<Installment> findWithFilters(
            @Param("status") InstallmentStatus status,
            @Param("dueDateFrom") LocalDate dueDateFrom,
            @Param("dueDateTo") LocalDate dueDateTo,
            @Param("amountMin") BigDecimal amountMin,
            @Param("amountMax") BigDecimal amountMax
    );
}
