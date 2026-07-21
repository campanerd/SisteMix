package org.siste.mix.order.usecase;

import jakarta.persistence.EntityNotFoundException;
import org.siste.mix.client.dto.CreateClientRequest;
import org.siste.mix.client.model.Client;
import org.siste.mix.order.dto.CreateOrderRequest;
import org.siste.mix.order.dto.FieldChange;
import org.siste.mix.order.enums.OrderHistoryAction;
import org.siste.mix.order.model.Order;
import org.siste.mix.order.model.OrderHistory;
import org.siste.mix.order.repository.OrderHistoryRepository;
import org.siste.mix.order.repository.OrderRepository;
import org.siste.mix.seller.dto.CreateSellerRequest;
import org.siste.mix.seller.model.Seller;
import org.siste.mix.user.dto.CreateUserRequest;
import org.siste.mix.user.enums.UserRole;
import org.siste.mix.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListOrderHistoryUseCaseTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderHistoryRepository orderHistoryRepository;
    @Mock
    private OrderHistoryRecorder orderHistoryRecorder;

    @InjectMocks
    private ListOrderHistoryUseCase useCase;

    @Test
    void should_return_deserialized_history_for_active_order() {
        var client = new Client(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        var seller = new Seller(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888"));
        var user = new User(new CreateUserRequest("Ana Admin", "ana@email.com", "123456", UserRole.ROLE_ADMIN), "hash");
        var request = new CreateOrderRequest("PED-001", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, 1L, 1L);
        var order = new Order(1L, request.orderNumber(), request.issueDate(), request.orderDate(),
                request.totalAmount(), request.totalInstallments(), request.notes(), client, seller, user, true);
        var history = new OrderHistory(order, user, OrderHistoryAction.UPDATE, "[]");

        // WHEN
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderHistoryRepository.findAllByOrderIdOrderByChangedAtDesc(1L)).thenReturn(List.of(history));
        when(orderHistoryRecorder.deserialize("[]")).thenReturn(List.of(new FieldChange("totalAmount", "300.00", "600.00")));

        // ASSERT
        var result = useCase.listHistory(1L);

        assertEquals(1, result.size());
        assertEquals(OrderHistoryAction.UPDATE, result.get(0).action());
        assertEquals("Ana Admin", result.get(0).changedByName());
        assertEquals(1, result.get(0).changes().size());
        assertEquals("totalAmount", result.get(0).changes().get(0).field());
    }

    @Test
    void should_throw_when_listing_history_of_nonexistent_order() {
        // WHEN
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // ASSERT
        assertThatThrownBy(() -> useCase.listHistory(1L)).isInstanceOf(EntityNotFoundException.class);
    }
}
