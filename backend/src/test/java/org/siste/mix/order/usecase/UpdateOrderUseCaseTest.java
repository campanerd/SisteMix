package org.siste.mix.order.usecase;

import org.siste.mix.client.dto.CreateClientRequest;
import org.siste.mix.client.model.Client;
import org.siste.mix.installment.enums.InstallmentStatus;
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
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private InstallmentRepository installmentRepository;
    @Mock
    private SellerRepository sellerRepository;
    @Mock
    private InstallmentGenerator installmentGenerator;
    @Mock
    private OrderHistoryRecorder orderHistoryRecorder;

    @InjectMocks
    private UpdateOrderUseCase useCase;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new User(new CreateUserRequest("Ana Admin", "ana@email.com", "123456", UserRole.ROLE_ADMIN), "hash");
        lenient().when(orderHistoryRecorder.buildChanges(any(), any(), any())).thenReturn(List.of());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(currentUser, null));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void should_recalculate_installments_when_amount_changes_and_none_paid() {
        var order = savedOrder(orderWith(new BigDecimal("300.00"), 3, LocalDate.of(2026, 1, 15)));

        // WHEN
        when(orderRepository.getReferenceById(1L)).thenReturn(order);
        when(installmentRepository.existsByOrderIdAndStatus(1L, InstallmentStatus.PAID)).thenReturn(false);

        // ASSERT
        useCase.update(new UpdateOrderRequest(1L, null, null, new BigDecimal("600.00"), null, null));

        verify(installmentRepository).deleteAllByOrderId(1L);
        verify(installmentGenerator).generate(order);
    }

    @Test
    void should_recalculate_installments_when_order_date_changes_and_none_paid() {
        var order = savedOrder(orderWith(new BigDecimal("300.00"), 3, LocalDate.of(2026, 1, 15)));

        // WHEN
        when(orderRepository.getReferenceById(1L)).thenReturn(order);
        when(installmentRepository.existsByOrderIdAndStatus(1L, InstallmentStatus.PAID)).thenReturn(false);

        // ASSERT
        useCase.update(new UpdateOrderRequest(1L, null, LocalDate.of(2026, 3, 1), null, null, null));

        verify(installmentRepository).deleteAllByOrderId(1L);
        verify(installmentGenerator).generate(order);
    }

    @Test
    void should_not_touch_installments_when_amount_and_date_are_unchanged() {
        var order = savedOrder(orderWith(new BigDecimal("300.00"), 3, LocalDate.of(2026, 1, 15)));

        // WHEN
        when(orderRepository.getReferenceById(1L)).thenReturn(order);

        // ASSERT
        useCase.update(new UpdateOrderRequest(1L, null, null, null, "nova observação", null));

        verify(installmentRepository, never()).existsByOrderIdAndStatus(anyLong(), any());
        verify(installmentRepository, never()).deleteAllByOrderId(anyLong());
        verify(installmentGenerator, never()).generate(any());
    }

    @Test
    void should_throw_when_amount_changes_and_installment_is_paid() {
        var order = savedOrder(orderWith(new BigDecimal("300.00"), 3, LocalDate.of(2026, 1, 15)));

        // WHEN
        when(orderRepository.getReferenceById(1L)).thenReturn(order);
        when(installmentRepository.existsByOrderIdAndStatus(1L, InstallmentStatus.PAID)).thenReturn(true);

        // ASSERT
        assertThatThrownBy(() -> useCase.update(new UpdateOrderRequest(1L, null, null, new BigDecimal("600.00"), null, null)))
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
        assertThatThrownBy(() -> useCase.update(new UpdateOrderRequest(1L, null, LocalDate.of(2026, 3, 1), null, null, null)))
                .isInstanceOf(OrderHasPaidInstallmentsException.class);

        verify(installmentRepository, never()).deleteAllByOrderId(anyLong());
    }

    @Test
    void should_record_history_after_update() {
        var order = savedOrder(orderWith(new BigDecimal("300.00"), 3, LocalDate.of(2026, 1, 15)));
        var data = new UpdateOrderRequest(1L, null, null, new BigDecimal("600.00"), "Nova nota", null);

        // WHEN
        when(orderRepository.getReferenceById(1L)).thenReturn(order);
        when(installmentRepository.existsByOrderIdAndStatus(1L, InstallmentStatus.PAID)).thenReturn(false);

        // ASSERT
        useCase.update(data);

        verify(orderHistoryRecorder).buildChanges(order, data, null);
        verify(orderHistoryRecorder).recordUpdate(order, currentUser, List.of());
    }

    private CreateOrderRequest orderWith(BigDecimal totalAmount, int totalInstallments, LocalDate orderDate) {
        return new CreateOrderRequest("PED-001", orderDate, orderDate, totalAmount, totalInstallments, null, 1L, 1L);
    }

    private Order savedOrder(CreateOrderRequest data) {
        var client = new Client(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        var seller = new Seller(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888"));
        return new Order(1L, data.orderNumber(), data.issueDate(), data.orderDate(),
                data.totalAmount(), data.totalInstallments(), data.notes(), client, seller, currentUser, true);
    }
}
