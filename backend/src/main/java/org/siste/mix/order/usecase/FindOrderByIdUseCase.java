package org.siste.mix.order.usecase;

import jakarta.persistence.EntityNotFoundException;
import org.siste.mix.order.dto.OrderResponse;
import org.siste.mix.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindOrderByIdUseCase {

    @Autowired
    private OrderRepository orderRepository;

    public OrderResponse findById(Long id) {
        var order = orderRepository.findById(id)
                .filter(o -> Boolean.TRUE.equals(o.getActive()))
                .orElseThrow(EntityNotFoundException::new);
        return new OrderResponse(order);
    }
}
