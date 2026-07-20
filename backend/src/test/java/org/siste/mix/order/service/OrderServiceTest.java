package org.siste.mix.order.service;

import org.siste.mix.client.dto.CreateClientRequest;
import org.siste.mix.client.model.Client;
import org.siste.mix.client.repository.ClientRepository;
import org.siste.mix.installment.enums.InstallmentStatus;
import org.siste.mix.installment.model.Installment;
import org.siste.mix.installment.repository.InstallmentRepository;
import org.siste.mix.order.dto.CreateOrderRequest;
import org.siste.mix.order.dto.UpdateOrderRequest;
import org.siste.mix.order.exception.OrderHasPaidInstallmentsException;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private InstallmentRepository installmentRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private OrderService service;

    private Client client;
    private Seller seller;
    private User createdBy;

    @BeforeEach
    void setUp() {
        client = new Client(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        seller = new Seller(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888"));
        createdBy = new User(new CreateUserRequest("Ana Admin", "ana@email.com", "123456", UserRole.ROLE_ADMIN), "hash");
        lenient().when(clientRepository.getReferenceById(1L)).thenReturn(client);
        lenient().when(sellerRepository.getReferenceById(1L)).thenReturn(seller);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(createdBy, null));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void should_create_correct_number_of_installments() {
        var request = orderWith(new BigDecimal("300.00"), 3, LocalDate.of(2026, 1, 15));

        // WHEN
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder(request));

        // ASSERT
        service.create(request);

        // InOrder
        InOrder inOrder = inOrder(orderRepository, installmentRepository);
        inOrder.verify(orderRepository).save(any(Order.class));
        inOrder.verify(installmentRepository, times(3)).save(any(Installment.class));
    }

    @Test
    void should_distribute_amount_with_rounding_on_last_installment() {
        var request = orderWith(new BigDecimal("100.00"), 3, LocalDate.of(2026, 1, 15));

        // WHEN
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder(request));

        // ASSERT
        service.create(request);

        var captor = ArgumentCaptor.forClass(Installment.class);

        // InOrder
        InOrder inOrder = inOrder(orderRepository, installmentRepository);
        inOrder.verify(orderRepository).save(any(Order.class));
        inOrder.verify(installmentRepository, times(3)).save(captor.capture());

        List<Installment> installments = captor.getAllValues();
        assertEquals(0, installments.get(0).getAmount().compareTo(new BigDecimal("33.33")));
        assertEquals(0, installments.get(1).getAmount().compareTo(new BigDecimal("33.33")));
        assertEquals(0, installments.get(2).getAmount().compareTo(new BigDecimal("33.34")));
    }

    @Test
    void should_distribute_equal_amounts_when_division_is_exact() {
        var request = orderWith(new BigDecimal("300.00"), 3, LocalDate.of(2026, 1, 15));

        // WHEN
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder(request));

        // ASSERT
        service.create(request);

        var captor = ArgumentCaptor.forClass(Installment.class);

        // InOrder
        InOrder inOrder = inOrder(orderRepository, installmentRepository);
        inOrder.verify(orderRepository).save(any(Order.class));
        inOrder.verify(installmentRepository, times(3)).save(captor.capture());

        captor.getAllValues().forEach(i ->
                assertEquals(0, i.getAmount().compareTo(new BigDecimal("100.00")))
        );
    }

    @Test
    void should_set_monthly_due_dates_from_order_date() {
        var orderDate = LocalDate.of(2026, 1, 15);
        var request = orderWith(new BigDecimal("200.00"), 2, orderDate);

        // WHEN
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder(request));

        // ASSERT
        service.create(request);

        var captor = ArgumentCaptor.forClass(Installment.class);

        // InOrder
        InOrder inOrder = inOrder(orderRepository, installmentRepository);
        inOrder.verify(orderRepository).save(any(Order.class));
        inOrder.verify(installmentRepository, times(2)).save(captor.capture());

        List<Installment> installments = captor.getAllValues();
        assertEquals(LocalDate.of(2026, 2, 15), installments.get(0).getDueDate());
        assertEquals(LocalDate.of(2026, 3, 15), installments.get(1).getDueDate());
    }

    @Test
    void should_recalculate_installments_when_amount_changes_and_none_paid() {
        var order = savedOrder(orderWith(new BigDecimal("300.00"), 3, LocalDate.of(2026, 1, 15)));

        // WHEN
        when(orderRepository.getReferenceById(1L)).thenReturn(order);
        when(installmentRepository.existsByOrderIdAndStatus(1L, InstallmentStatus.PAID)).thenReturn(false);

        // ASSERT
        service.update(new UpdateOrderRequest(1L, null, null, new BigDecimal("600.00"), null, null));

        InOrder inOrder = inOrder(installmentRepository);
        inOrder.verify(installmentRepository).deleteAllByOrderId(1L);
        inOrder.verify(installmentRepository, times(3)).save(any(Installment.class));
    }

    @Test
    void should_recalculate_installments_when_order_date_changes_and_none_paid() {
        var order = savedOrder(orderWith(new BigDecimal("300.00"), 3, LocalDate.of(2026, 1, 15)));

        // WHEN
        when(orderRepository.getReferenceById(1L)).thenReturn(order);
        when(installmentRepository.existsByOrderIdAndStatus(1L, InstallmentStatus.PAID)).thenReturn(false);

        // ASSERT
        service.update(new UpdateOrderRequest(1L, null, LocalDate.of(2026, 3, 1), null, null, null));

        InOrder inOrder = inOrder(installmentRepository);
        inOrder.verify(installmentRepository).deleteAllByOrderId(1L);
        inOrder.verify(installmentRepository, times(3)).save(any(Installment.class));
    }

    @Test
    void should_not_touch_installments_when_amount_and_date_are_unchanged() {
        var order = savedOrder(orderWith(new BigDecimal("300.00"), 3, LocalDate.of(2026, 1, 15)));

        // WHEN
        when(orderRepository.getReferenceById(1L)).thenReturn(order);

        // ASSERT
        service.update(new UpdateOrderRequest(1L, null, null, null, "nova observação", null));

        verify(installmentRepository, never()).existsByOrderIdAndStatus(anyLong(), any());
        verify(installmentRepository, never()).deleteAllByOrderId(anyLong());
    }

    @Test
    void should_throw_when_amount_changes_and_installment_is_paid() {
        var order = savedOrder(orderWith(new BigDecimal("300.00"), 3, LocalDate.of(2026, 1, 15)));

        // WHEN
        when(orderRepository.getReferenceById(1L)).thenReturn(order);
        when(installmentRepository.existsByOrderIdAndStatus(1L, InstallmentStatus.PAID)).thenReturn(true);

        // ASSERT
        assertThatThrownBy(() -> service.update(new UpdateOrderRequest(1L, null, null, new BigDecimal("600.00"), null, null)))
                .isInstanceOf(OrderHasPaidInstallmentsException.class);

        verify(installmentRepository, never()).deleteAllByOrderId(anyLong());
    }

    @Test
    void should_throw_when_order_date_changes_and_installment_is_paid() {
        var order = savedOrder(orderWith(new BigDecimal("300.00"), 3, LocalDate.of(2026, 1, 15)));

        // WHEN
        when(orderRepository.getReferenceById(1L)).thenReturn(order);
        when(installmentRepository.existsByOrderIdAndStatus(1L, InstallmentStatus.PAID)).thenReturn(true);

        // ASSERT
        assertThatThrownBy(() -> service.update(new UpdateOrderRequest(1L, null, LocalDate.of(2026, 3, 1), null, null, null)))
                .isInstanceOf(OrderHasPaidInstallmentsException.class);

        verify(installmentRepository, never()).deleteAllByOrderId(anyLong());
    }

    private CreateOrderRequest orderWith(BigDecimal totalAmount, int totalInstallments, LocalDate orderDate) {
        return new CreateOrderRequest("PED-001", orderDate, orderDate, totalAmount, totalInstallments, null, 1L, 1L);
    }

    private Order savedOrder(CreateOrderRequest data) {
        return new Order(1L, data.orderNumber(), data.issueDate(), data.orderDate(),
                data.totalAmount(), data.totalInstallments(), data.notes(), client, seller, createdBy, true);
    }
}
