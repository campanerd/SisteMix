package org.siste.mix.order.repository;

import org.siste.mix.order.model.OrderHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {

    @EntityGraph(attributePaths = {"user"})
    List<OrderHistory> findAllByOrderIdOrderByChangedAtDesc(Long orderId);
}
