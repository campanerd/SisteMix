package org.siste.mix.order.usecase;

import org.siste.mix.order.repository.OrderRepository;
import org.siste.mix.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteOrderUseCase {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderHistoryRecorder orderHistoryRecorder;

    @Transactional
    public void delete(Long id) {
        var order = orderRepository.getReferenceById(id);
        order.deactivate();

        var currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        orderHistoryRecorder.recordDelete(order, currentUser);
    }
}
