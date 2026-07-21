package org.siste.mix.order.usecase;

import jakarta.persistence.EntityNotFoundException;
import org.siste.mix.order.dto.OrderHistoryResponse;
import org.siste.mix.order.repository.OrderHistoryRepository;
import org.siste.mix.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListOrderHistoryUseCase {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private OrderHistoryRecorder orderHistoryRecorder;

    public List<OrderHistoryResponse> listHistory(Long orderId) {
        orderRepository.findById(orderId)
                .filter(o -> Boolean.TRUE.equals(o.getActive()))
                .orElseThrow(EntityNotFoundException::new);

        return orderHistoryRepository.findAllByOrderIdOrderByChangedAtDesc(orderId).stream()
                .map(history -> new OrderHistoryResponse(
                        history.getId(),
                        history.getAction(),
                        history.getChangedAt(),
                        history.getUser().getName(),
                        orderHistoryRecorder.deserialize(history.getChanges())
                ))
                .toList();
    }
}
