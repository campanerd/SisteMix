package org.siste.mix.order.usecase;

import jakarta.persistence.EntityNotFoundException;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindOrderByIdUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private FindOrderByIdUseCase useCase;

    @Test
    void should_return_order_when_active() {
        var client = new Client(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        var seller = new Seller(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888"));
        var createdBy = new User(new CreateUserRequest("Ana Admin", "ana@email.com", "123456", UserRole.ROLE_ADMIN), "hash");
        var request = new CreateOrderRequest("PED-001", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, 1L, 1L);
        var order = new Order(1L, request.orderNumber(), request.issueDate(), request.orderDate(),
                request.totalAmount(), request.totalInstallments(), request.notes(), client, seller, createdBy, true);

        // WHEN
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // ASSERT
        var response = useCase.findById(1L);

        assertEquals("PED-001", response.orderNumber());
    }

    @Test
    void should_throw_when_order_is_inactive() {
        var client = new Client(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        var seller = new Seller(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888"));
        var createdBy = new User(new CreateUserRequest("Ana Admin", "ana@email.com", "123456", UserRole.ROLE_ADMIN), "hash");
        var request = new CreateOrderRequest("PED-001", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, 1L, 1L);
        var order = new Order(1L, request.orderNumber(), request.issueDate(), request.orderDate(),
                request.totalAmount(), request.totalInstallments(), request.notes(), client, seller, createdBy, false);

        // WHEN
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // ASSERT
        assertThatThrownBy(() -> useCase.findById(1L)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void should_throw_when_order_does_not_exist() {
        // WHEN
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // ASSERT
        assertThatThrownBy(() -> useCase.findById(1L)).isInstanceOf(EntityNotFoundException.class);
    }
}
