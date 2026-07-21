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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderHistoryRecorder orderHistoryRecorder;

    @InjectMocks
    private DeleteOrderUseCase useCase;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new User(new CreateUserRequest("Ana Admin", "ana@email.com", "123456", UserRole.ROLE_ADMIN), "hash");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(currentUser, null));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void should_deactivate_order_and_record_history() {
        var client = new Client(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        var seller = new Seller(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888"));
        var request = new CreateOrderRequest("PED-001", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, 1L, 1L);
        var order = new Order(1L, request.orderNumber(), request.issueDate(), request.orderDate(),
                request.totalAmount(), request.totalInstallments(), request.notes(), client, seller, currentUser, true);

        // WHEN
        when(orderRepository.getReferenceById(1L)).thenReturn(order);

        // ASSERT
        useCase.delete(1L);

        assertFalse(order.getActive());
        verify(orderHistoryRecorder).recordDelete(order, currentUser);
    }
}
