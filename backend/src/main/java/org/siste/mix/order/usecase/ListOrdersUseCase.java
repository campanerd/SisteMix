package org.siste.mix.order.usecase;

import org.siste.mix.order.dto.OrderSummary;
import org.siste.mix.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListOrdersUseCase {

    @Autowired
    private OrderRepository orderRepository;

    public Page<OrderSummary> list(Pageable pageable) {
        return orderRepository.findAllByActiveTrue(pageable).map(OrderSummary::new);
    }
}
