package org.siste.mix.order.web;

import org.siste.mix.client.model.Client;
import org.siste.mix.order.dto.CreateOrderRequest;
import org.siste.mix.order.dto.FieldChange;
import org.siste.mix.order.dto.OrderHistoryResponse;
import org.siste.mix.order.dto.OrderResponse;
import org.siste.mix.order.dto.OrderSummary;
import org.siste.mix.order.dto.UpdateOrderRequest;
import org.siste.mix.order.enums.OrderHistoryAction;
import org.siste.mix.order.model.Order;
import org.siste.mix.order.usecase.CreateOrderUseCase;
import org.siste.mix.order.usecase.DeleteOrderUseCase;
import org.siste.mix.order.usecase.FindOrderByIdUseCase;
import org.siste.mix.order.usecase.ListOrderHistoryUseCase;
import org.siste.mix.order.usecase.ListOrdersUseCase;
import org.siste.mix.order.usecase.UpdateOrderUseCase;
import org.siste.mix.seller.model.Seller;
import org.siste.mix.user.dto.CreateUserRequest;
import org.siste.mix.user.enums.UserRole;
import org.siste.mix.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private CreateOrderUseCase createOrderUseCase;
    @Mock
    private ListOrdersUseCase listOrdersUseCase;
    @Mock
    private UpdateOrderUseCase updateOrderUseCase;
    @Mock
    private DeleteOrderUseCase deleteOrderUseCase;
    @Mock
    private FindOrderByIdUseCase findOrderByIdUseCase;
    @Mock
    private ListOrderHistoryUseCase listOrderHistoryUseCase;

    @InjectMocks
    private OrderController controller;

    private Order order;
    private OrderResponse orderResponse;
    private UriComponentsBuilder uriBuilder;

    @BeforeEach
    void setUp() {
        var client = new Client(1L, "João Silva", "11999999999", "12345678900", "joao@email.com", true);
        var seller = new Seller(1L, "Maria Souza", "98765432100", "11988888888", true);
        var createdBy = new User(new CreateUserRequest("Ana Admin", "ana@email.com", "123456", UserRole.ROLE_ADMIN), "hash");
        order = new Order(1L, "PED-001",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, client, seller, createdBy, true);
        orderResponse = new OrderResponse(1L, "PED-001",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null,
                1L, "João Silva", 1L, "Maria Souza", "Ana Admin");
        uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");
    }

    @Test
    void should_create_order_and_return_201() {
        var request = new CreateOrderRequest("PED-001",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, 1L, 1L);

        // WHEN
        when(createOrderUseCase.create(any(CreateOrderRequest.class))).thenReturn(orderResponse);

        // ASSERT
        var response = controller.create(request, uriBuilder);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
        assertEquals("PED-001", response.getBody().orderNumber());
        assertEquals("João Silva", response.getBody().clientName());

        // InOrder
        InOrder inOrder = inOrder(createOrderUseCase);
        inOrder.verify(createOrderUseCase).create(any(CreateOrderRequest.class));
    }

    @Test
    void should_list_active_orders() {
        var page = new PageImpl<>(List.of(new OrderSummary(order)));

        // WHEN
        when(listOrdersUseCase.list(any(Pageable.class))).thenReturn(page);

        // ASSERT
        var response = controller.list(Pageable.ofSize(10));

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("PED-001", response.getBody().getContent().get(0).orderNumber());
        assertEquals("João Silva", response.getBody().getContent().get(0).clientName());

        // InOrder
        InOrder inOrder = inOrder(listOrdersUseCase);
        inOrder.verify(listOrdersUseCase).list(any(Pageable.class));
    }

    @Test
    void should_update_order_and_return_200() {
        var request = new UpdateOrderRequest(1L, null, null, null, "New notes", null);
        var updated = new OrderResponse(1L, "PED-001",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, "New notes",
                1L, "João Silva", 1L, "Maria Souza", "Ana Admin");

        // WHEN
        when(updateOrderUseCase.update(any(UpdateOrderRequest.class))).thenReturn(updated);

        // ASSERT
        var response = controller.update(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("New notes", response.getBody().notes());

        // InOrder
        InOrder inOrder = inOrder(updateOrderUseCase);
        inOrder.verify(updateOrderUseCase).update(any(UpdateOrderRequest.class));
    }

    @Test
    void should_update_seller_in_order() {
        var request = new UpdateOrderRequest(1L, null, null, null, null, 2L);
        var updated = new OrderResponse(1L, "PED-001",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null,
                1L, "João Silva", 2L, "Carlos Lima", "Ana Admin");

        // WHEN
        when(updateOrderUseCase.update(any(UpdateOrderRequest.class))).thenReturn(updated);

        // ASSERT
        var response = controller.update(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Carlos Lima", response.getBody().sellerName());

        // InOrder
        InOrder inOrder = inOrder(updateOrderUseCase);
        inOrder.verify(updateOrderUseCase).update(any(UpdateOrderRequest.class));
    }

    @Test
    void should_delete_order_and_return_204() {
        // WHEN
        doNothing().when(deleteOrderUseCase).delete(1L);

        // ASSERT
        var response = controller.delete(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // InOrder
        InOrder inOrder = inOrder(deleteOrderUseCase);
        inOrder.verify(deleteOrderUseCase).delete(1L);
    }

    @Test
    void should_return_order_detail_with_200() {
        // WHEN
        when(findOrderByIdUseCase.findById(1L)).thenReturn(orderResponse);

        // ASSERT
        var response = controller.findById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
        assertEquals(0, response.getBody().totalAmount().compareTo(new BigDecimal("300.00")));
        assertEquals(3, response.getBody().totalInstallments());

        // InOrder
        InOrder inOrder = inOrder(findOrderByIdUseCase);
        inOrder.verify(findOrderByIdUseCase).findById(1L);
    }

    @Test
    void should_return_order_history_with_200() {
        var historyResponse = new OrderHistoryResponse(1L, OrderHistoryAction.UPDATE, LocalDateTime.now(),
                "Ana Admin", List.of(new FieldChange("totalAmount", "300.00", "600.00")));

        // WHEN
        when(listOrderHistoryUseCase.listHistory(1L)).thenReturn(List.of(historyResponse));

        // ASSERT
        var response = controller.history(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(OrderHistoryAction.UPDATE, response.getBody().get(0).action());

        // InOrder
        InOrder inOrder = inOrder(listOrderHistoryUseCase);
        inOrder.verify(listOrderHistoryUseCase).listHistory(1L);
    }
}
