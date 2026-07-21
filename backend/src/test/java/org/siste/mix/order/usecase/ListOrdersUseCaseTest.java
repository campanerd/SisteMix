package org.siste.mix.order.usecase;

import org.siste.mix.client.dto.CreateClientRequest;
import org.siste.mix.client.model.Client;
import org.siste.mix.order.dto.CreateOrderRequest;
import org.siste.mix.order.model.Order;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListOrdersUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ListOrdersUseCase useCase;

    @Test
    void should_list_active_orders_as_summary() {
        var client = new Client(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        var seller = new Seller(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888"));
        var createdBy = new User(new CreateUserRequest("Ana Admin", "ana@email.com", "123456", UserRole.ROLE_ADMIN), "hash");
        var request = new CreateOrderRequest("PED-001", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, 1L, 1L);
        var order = new Order(1L, request.orderNumber(), request.issueDate(), request.orderDate(),
                request.totalAmount(), request.totalInstallments(), request.notes(), client, seller, createdBy, true);

        // WHEN
        when(orderRepository.findAllByActiveTrue(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(order)));

        // ASSERT
        var result = useCase.list(Pageable.ofSize(10));

        assertEquals(1, result.getContent().size());
        assertEquals("PED-001", result.getContent().get(0).orderNumber());
    }
}
