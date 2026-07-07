package org.siste.mix.order.service;

import jakarta.persistence.EntityNotFoundException;
import org.siste.mix.client.repository.ClientRepository;
import org.siste.mix.installment.model.Installment;
import org.siste.mix.installment.repository.InstallmentRepository;
import org.siste.mix.order.dto.CreateOrderRequest;
import org.siste.mix.order.dto.OrderResponse;
import org.siste.mix.order.dto.OrderSummary;
import org.siste.mix.order.dto.UpdateOrderRequest;
import org.siste.mix.order.model.Order;
import org.siste.mix.order.repository.OrderRepository;
import org.siste.mix.seller.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        var order = orderRepository.save(new Order(data, client, seller));
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
        order.update(data, seller);
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
