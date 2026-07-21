package org.siste.mix.order.usecase;

import org.siste.mix.client.dto.CreateClientRequest;
import org.siste.mix.client.model.Client;
import org.siste.mix.order.dto.CreateOrderRequest;
import org.siste.mix.order.dto.FieldChange;
import org.siste.mix.order.dto.UpdateOrderRequest;
import org.siste.mix.order.enums.OrderHistoryAction;
import org.siste.mix.order.model.Order;
import org.siste.mix.order.model.OrderHistory;
import org.siste.mix.order.repository.OrderHistoryRepository;
import org.siste.mix.seller.dto.CreateSellerRequest;
import org.siste.mix.seller.model.Seller;
import org.siste.mix.user.dto.CreateUserRequest;
import org.siste.mix.user.enums.UserRole;
import org.siste.mix.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderHistoryRecorderTest {

    @Mock
    private OrderHistoryRepository orderHistoryRepository;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private OrderHistoryRecorder recorder;

    private Order order;
    private User user;

    @BeforeEach
    void setUp() {
        var client = new Client(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        var seller = new Seller(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888"));
        user = new User(new CreateUserRequest("Ana Admin", "ana@email.com", "123456", UserRole.ROLE_ADMIN), "hash");
        var request = new CreateOrderRequest("PED-001", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, 1L, 1L);
        order = new Order(1L, request.orderNumber(), request.issueDate(), request.orderDate(),
                request.totalAmount(), request.totalInstallments(), request.notes(), client, seller, user, true);
    }

    @Test
    void should_detect_changed_fields() {
        var newSeller = new Seller(new CreateSellerRequest("Carlos Lima", "11122233344", "11977777777"));

        var changes = recorder.buildChanges(order, new UpdateOrderRequest(1L, null, null, new BigDecimal("600.00"), "Nova nota", 2L), newSeller);

        assertEquals(3, changes.size());
        assertTrue(changes.stream().anyMatch(c -> c.field().equals("totalAmount") && c.to().equals("600.00")));
        assertTrue(changes.stream().anyMatch(c -> c.field().equals("notes") && c.to().equals("Nova nota")));
        assertTrue(changes.stream().anyMatch(c -> c.field().equals("sellerName") && c.to().equals("Carlos Lima")));
    }

    @Test
    void should_detect_no_changes_when_nothing_provided() {
        var changes = recorder.buildChanges(order, new UpdateOrderRequest(1L, null, null, null, null, null), null);

        assertTrue(changes.isEmpty());
    }

    @Test
    void should_save_history_on_record_update_when_changes_present() {
        var changes = List.of(new FieldChange("totalAmount", "300.00", "600.00"));

        recorder.recordUpdate(order, user, changes);

        var captor = ArgumentCaptor.forClass(OrderHistory.class);
        verify(orderHistoryRepository).save(captor.capture());

        var history = captor.getValue();
        assertEquals(OrderHistoryAction.UPDATE, history.getAction());
        assertEquals(user, history.getUser());
        assertEquals(order, history.getOrder());
    }

    @Test
    void should_not_save_history_on_record_update_when_changes_empty() {
        recorder.recordUpdate(order, user, List.of());

        verify(orderHistoryRepository, never()).save(any());
    }

    @Test
    void should_save_history_on_record_delete() {
        recorder.recordDelete(order, user);

        var captor = ArgumentCaptor.forClass(OrderHistory.class);
        verify(orderHistoryRepository).save(captor.capture());

        var history = captor.getValue();
        assertEquals(OrderHistoryAction.DELETE, history.getAction());
        assertEquals(user, history.getUser());
        assertNull(history.getChanges());
    }

    @Test
    void should_deserialize_valid_json() {
        var json = objectMapper.writeValueAsString(List.of(new FieldChange("totalAmount", "300.00", "600.00")));

        var changes = recorder.deserialize(json);

        assertEquals(1, changes.size());
        assertEquals("totalAmount", changes.get(0).field());
    }

    @Test
    void should_return_empty_list_when_deserializing_null() {
        var changes = recorder.deserialize(null);

        assertTrue(changes.isEmpty());
    }
}
