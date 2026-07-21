package org.siste.mix.order.usecase;

import org.siste.mix.installment.enums.InstallmentStatus;
import org.siste.mix.installment.repository.InstallmentRepository;
import org.siste.mix.order.dto.OrderResponse;
import org.siste.mix.order.dto.UpdateOrderRequest;
import org.siste.mix.order.exception.OrderHasPaidInstallmentsException;
import org.siste.mix.order.repository.OrderRepository;
import org.siste.mix.seller.repository.SellerRepository;
import org.siste.mix.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateOrderUseCase {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InstallmentRepository installmentRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private InstallmentGenerator installmentGenerator;

    @Autowired
    private OrderHistoryRecorder orderHistoryRecorder;

    @Transactional
    public OrderResponse update(UpdateOrderRequest data) {
        var order = orderRepository.getReferenceById(data.id());
        var seller = data.sellerId() != null
                ? sellerRepository.getReferenceById(data.sellerId())
                : null;

        var recalculateInstallments = data.totalAmount() != null || data.orderDate() != null;
        if (recalculateInstallments && installmentRepository.existsByOrderIdAndStatus(data.id(), InstallmentStatus.PAID)) {
            throw new OrderHasPaidInstallmentsException();
        }

        var changes = orderHistoryRecorder.buildChanges(order, data, seller);

        order.update(data, seller);

        if (recalculateInstallments) {
            installmentRepository.deleteAllByOrderId(order.getId());
            installmentGenerator.generate(order);
        }

        var currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        orderHistoryRecorder.recordUpdate(order, currentUser, changes);

        return new OrderResponse(order);
    }
}
