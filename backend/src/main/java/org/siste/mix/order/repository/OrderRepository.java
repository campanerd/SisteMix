package org.siste.mix.order.repository;

import org.siste.mix.order.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"client", "seller"})
    Page<Order> findAllByActiveTrue(Pageable pageable);

    @EntityGraph(attributePaths = {"client", "seller"})
    @Override
    Optional<Order> findById(Long id);
}
