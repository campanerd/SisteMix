package org.siste.mix.order.usecase;

import org.siste.mix.client.dto.CreateClientRequest;
import org.siste.mix.client.model.Client;
import org.siste.mix.client.repository.ClientRepository;
import org.siste.mix.order.dto.CreateOrderRequest;
import org.siste.mix.order.model.Order;
import org.siste.mix.order.repository.OrderRepository;
import org.siste.mix.seller.dto.CreateSellerRequest;
import org.siste.mix.seller.model.Seller;
import org.siste.mix.seller.repository.SellerRepository;
import org.siste.mix.user.dto.CreateUserRequest;
import org.siste.mix.user.enums.UserRole;
import org.siste.mix.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private SellerRepository sellerRepository;
    @Mock
    private InstallmentGenerator installmentGenerator;

    @InjectMocks
    private CreateOrderUseCase useCase;

    private User createdBy;

    @BeforeEach
    void setUp() {
        var client = new Client(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        var seller = new Seller(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888"));
        createdBy = new User(new CreateUserRequest("Ana Admin", "ana@email.com", "123456", UserRole.ROLE_ADMIN), "hash");
        when(clientRepository.getReferenceById(1L)).thenReturn(client);
        when(sellerRepository.getReferenceById(1L)).thenReturn(seller);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(createdBy, null));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void should_create_order_and_generate_installments() {
        var request = new CreateOrderRequest("PED-001", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, 1L, 1L);

        // WHEN
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ASSERT
        var response = useCase.create(request);

        assertEquals("PED-001", response.orderNumber());
        assertEquals("Ana Admin", response.createdByName());

        // InOrder
        InOrder inOrder = inOrder(orderRepository, installmentGenerator);
        inOrder.verify(orderRepository).save(any(Order.class));
        inOrder.verify(installmentGenerator).generate(any(Order.class));
    }
}
