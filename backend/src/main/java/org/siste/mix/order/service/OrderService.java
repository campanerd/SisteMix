package org.siste.mix.order.service;

import jakarta.persistence.EntityNotFoundException;
import org.siste.mix.client.repository.ClientRepository;
import org.siste.mix.installment.enums.InstallmentStatus;
import org.siste.mix.installment.model.Installment;
import org.siste.mix.installment.repository.InstallmentRepository;
import org.siste.mix.order.dto.CreateOrderRequest;
import org.siste.mix.order.dto.OrderResponse;
import org.siste.mix.order.dto.OrderSummary;
import org.siste.mix.order.dto.UpdateOrderRequest;
import org.siste.mix.order.exception.OrderHasPaidInstallmentsException;
import org.siste.mix.order.model.Order;
import org.siste.mix.order.repository.OrderRepository;
import org.siste.mix.seller.repository.SellerRepository;
import org.siste.mix.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InstallmentRepository installmentRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Transactional
    public OrderResponse create(CreateOrderRequest data) {
        var client = clientRepository.getReferenceById(data.clientId());
        var seller = sellerRepository.getReferenceById(data.sellerId());
        var currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var order = orderRepository.save(new Order(data, client, seller, currentUser));
        generateInstallments(order);
        return new OrderResponse(order);
    }

    public Page<OrderSummary> list(Pageable pageable) {
        return orderRepository.findAllByActiveTrue(pageable).map(OrderSummary::new);
    }

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

        order.update(data, seller);

        if (recalculateInstallments) {
            installmentRepository.deleteAllByOrderId(order.getId());
            generateInstallments(order);
        }

        return new OrderResponse(order);
    }

    @Transactional
    public void delete(Long id) {
        var order = orderRepository.getReferenceById(id);
        order.deactivate();
    }

    public OrderResponse findById(Long id) {
        var order = orderRepository.findById(id)
                .filter(o -> Boolean.TRUE.equals(o.getActive()))
                .orElseThrow(EntityNotFoundException::new);
        return new OrderResponse(order);
    }

    private void generateInstallments(Order order) {
        int total = order.getTotalInstallments();
        BigDecimal installmentAmount = order.getTotalAmount()
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.DOWN);
        BigDecimal lastAmount = order.getTotalAmount()
                .subtract(installmentAmount.multiply(BigDecimal.valueOf(total - 1)));

        for (int i = 1; i <= total; i++) {
            BigDecimal amount = (i == total) ? lastAmount : installmentAmount;
            var dueDate = order.getOrderDate().plusMonths(i);
            installmentRepository.save(new Installment(i, amount, dueDate, order));
        }
    }
}
