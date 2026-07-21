package org.siste.mix.order.usecase;

import org.siste.mix.order.dto.FieldChange;
import org.siste.mix.order.dto.UpdateOrderRequest;
import org.siste.mix.order.enums.OrderHistoryAction;
import org.siste.mix.order.model.Order;
import org.siste.mix.order.model.OrderHistory;
import org.siste.mix.order.repository.OrderHistoryRepository;
import org.siste.mix.seller.model.Seller;
import org.siste.mix.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderHistoryRecorder {

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public List<FieldChange> buildChanges(Order order, UpdateOrderRequest data, Seller newSeller) {
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

    public void recordUpdate(Order order, User user, List<FieldChange> changes) {
        if (changes.isEmpty()) {
            return;
        }
        orderHistoryRepository.save(new OrderHistory(order, user, OrderHistoryAction.UPDATE, serialize(changes)));
    }

    public void recordDelete(Order order, User user) {
        orderHistoryRepository.save(new OrderHistory(order, user, OrderHistoryAction.DELETE, null));
    }

    public List<FieldChange> deserialize(String changes) {
        if (changes == null) {
            return List.of();
        }
        try {
            return objectMapper.readValue(changes, new TypeReference<List<FieldChange>>() {});
        } catch (JacksonException e) {
            throw new RuntimeException("Erro ao desserializar histórico de alterações", e);
        }
    }

    private String serialize(List<FieldChange> changes) {
        try {
            return objectMapper.writeValueAsString(changes);
        } catch (JacksonException e) {
            throw new RuntimeException("Erro ao serializar histórico de alterações", e);
        }
    }
}
