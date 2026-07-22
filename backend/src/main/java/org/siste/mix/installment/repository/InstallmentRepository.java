package org.siste.mix.installment.repository;

import org.siste.mix.installment.enums.InstallmentStatus;
import org.siste.mix.installment.model.Installment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface InstallmentRepository extends JpaRepository<Installment, Long>, JpaSpecificationExecutor<Installment> {

    @EntityGraph(attributePaths = {"order", "order.client"})
    @Override
    List<Installment> findAll(Specification<Installment> spec);

    @EntityGraph(attributePaths = {"order", "order.client"})
    @Override
    List<Installment> findAll(Specification<Installment> spec, Sort sort);

    boolean existsByOrderIdAndStatus(Long orderId, InstallmentStatus status);

    void deleteAllByOrderId(Long orderId);

    @Query(value = """
            SELECT x.id FROM (
                SELECT a.id, ROW_NUMBER() OVER (PARTITION BY a.order_id ORDER BY a.due_date ASC) AS rn
                FROM installments a
                JOIN orders b ON a.order_id = b.id
                WHERE a.status <> 'PAID' AND b.active = true
            ) x
            WHERE x.rn = 1
            """, nativeQuery = true)
    List<Long> findNextUnpaidInstallmentIdsPerOrder();

    @Query("""
            SELECT i.status AS status, COUNT(i) AS count, COALESCE(SUM(i.amount), 0) AS totalAmount
            FROM Installment i
            WHERE i.order.active = true
            GROUP BY i.status
            """)
    List<InstallmentStatusAggregate> aggregateActiveByStatus();
}
