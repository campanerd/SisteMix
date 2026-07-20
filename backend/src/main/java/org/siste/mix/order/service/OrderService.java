package org.siste.mix.order.service;

import jakarta.persistence.EntityNotFoundException;
import org.siste.mix.client.repository.ClientRepository;
import org.siste.mix.installment.enums.InstallmentStatus;
import org.siste.mix.installment.model.Installment;
import org.siste.mix.installment.repository.InstallmentRepository;
import org.siste.mix.order.dto.CreateOrderRequest;
import org.siste.mix.order.dto.FieldChange;
import org.siste.mix.order.dto.OrderHistoryResponse;
import org.siste.mix.order.dto.OrderResponse;
import org.siste.mix.order.dto.OrderSummary;
import org.siste.mix.order.dto.UpdateOrderRequest;
import org.siste.mix.order.enums.OrderHistoryAction;
import org.siste.mix.order.exception.OrderHasPaidInstallmentsException;
import org.siste.mix.order.model.Order;
import org.siste.mix.order.model.OrderHistory;
import org.siste.mix.order.repository.OrderHistoryRepository;
import org.siste.mix.order.repository.OrderRepository;
import org.siste.mix.seller.model.Seller;
import org.siste.mix.seller.repository.SellerRepository;
import org.siste.mix.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private ObjectMapper objectMapper;




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

        var changes = buildChanges(order, data, seller);

        order.update(data, seller);

        if (recalculateInstallments) {
            installmentRepository.deleteAllByOrderId(order.getId());
            generateInstallments(order);
        }

        if (!changes.isEmpty()) {
            var currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            orderHistoryRepository.save(new OrderHistory(order, currentUser, OrderHistoryAction.UPDATE, serializeChanges(changes)));
        }

        return new OrderResponse(order);
    }

    @Transactional
    public void delete(Long id) {
        var order = orderRepository.getReferenceById(id);
        order.deactivate();

        var currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        orderHistoryRepository.save(new OrderHistory(order, currentUser, OrderHistoryAction.DELETE, null));
    }

    public OrderResponse findById(Long id) {
        var order = orderRepository.findById(id)
                .filter(o -> Boolean.TRUE.equals(o.getActive()))
                .orElseThrow(EntityNotFoundException::new);
        return new OrderResponse(order);
    }

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
                        deserializeChanges(history.getChanges())
                ))
                .toList();
    }

    private List<FieldChange> buildChanges(Order order, UpdateOrderRequest data, Seller newSeller) {
        var changes = new ArrayList<FieldChange>();

        if (data.issueDate() != null && !data.issueDate().equals(order.getIssueDate())) {
            changes.add(new FieldChange("issueDate", String.valueOf(order.getIssueDate()), String.valueOf(data.issueDate())));
        }
        if (data.orderDate() != null && !data.orderDate().equals(order.getOrderDate())) {
            changes.add(new FieldChange("orderDate", String.valueOf(order.getOrderDate()), String.valueOf(data.orderDate())));
        }
        if (data.totalAmount() != null && data.totalAmount().compareTo(order.getTotalAmount()) != 0) {
            changes.add(new FieldChange("totalAmount", String.valueOf(order.getTotalAmount()), String.valueOf(data.totalAmount())));
        }
        if (data.notes() != null && !data.notes().equals(order.getNotes())) {
            changes.add(new FieldChange("notes", order.getNotes(), data.notes()));
        }
        if (newSeller != null && !newSeller.getName().equals(order.getSeller().getName())) {
            changes.add(new FieldChange("sellerName", order.getSeller().getName(), newSeller.getName()));
        }

        return changes;
    }

    private String serializeChanges(List<FieldChange> changes) {
        try {
            return objectMapper.writeValueAsString(changes);
        } catch (JacksonException e) {
            throw new RuntimeException("Erro ao serializar histórico de alterações", e);
        }
    }

    private List<FieldChange> deserializeChanges(String changes) {
        if (changes == null) {
            return List.of();
        }
        try {
            return objectMapper.readValue(changes, new TypeReference<List<FieldChange>>() {});
        } catch (JacksonException e) {
            throw new RuntimeException("Erro ao desserializar histórico de alterações", e);
        }
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
