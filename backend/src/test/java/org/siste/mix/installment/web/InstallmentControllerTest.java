package org.siste.mix.installment.web;

import org.siste.mix.client.dto.CreateClientRequest;
import org.siste.mix.client.model.Client;
import org.siste.mix.installment.dto.InstallmentResponse;
import org.siste.mix.installment.dto.InstallmentSummary;
import org.siste.mix.installment.dto.UpdateInstallmentStatusRequest;
import org.siste.mix.installment.enums.InstallmentStatus;
import org.siste.mix.installment.model.Installment;
import org.siste.mix.installment.usecase.FindInstallmentByIdUseCase;
import org.siste.mix.installment.usecase.ListInstallmentsByOrderUseCase;
import org.siste.mix.installment.usecase.ListInstallmentsUseCase;
import org.siste.mix.installment.usecase.UpdateInstallmentStatusUseCase;
import org.siste.mix.order.model.Order;
import org.siste.mix.seller.dto.CreateSellerRequest;
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
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallmentControllerTest {

    @Mock
    private ListInstallmentsUseCase listInstallmentsUseCase;
    @Mock
    private FindInstallmentByIdUseCase findInstallmentByIdUseCase;
    @Mock
    private ListInstallmentsByOrderUseCase listInstallmentsByOrderUseCase;
    @Mock
    private UpdateInstallmentStatusUseCase updateInstallmentStatusUseCase;

    @InjectMocks
    private InstallmentController controller;

    private InstallmentSummary summary;
    private InstallmentResponse detail;

    @BeforeEach
    void setUp() {
        var client = new Client(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        var seller = new Seller(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888"));
        var createdBy = new User(new CreateUserRequest("Ana Admin", "ana@email.com", "123456", UserRole.ROLE_ADMIN), "hash");
        var order = new Order(1L, "PED-001",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, client, seller, createdBy, true);
        var installment = new Installment(1L, 1, new BigDecimal("100.00"),
                LocalDate.of(2026, 2, 15), InstallmentStatus.PENDING, null, order);
        summary = new InstallmentSummary(installment);
        detail = new InstallmentResponse(installment);
    }

    @Test
    void should_list_installments_without_filters() {
        // WHEN
        when(listInstallmentsUseCase.list(any(), any(), any(), any(), any(), anyBoolean())).thenReturn(List.of(summary));

        // ASSERT
        var response = controller.list(null, null, null, null, null, false);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("PED-001", response.getBody().get(0).orderNumber());
        assertEquals("João Silva", response.getBody().get(0).clientName());
        assertEquals(InstallmentStatus.PENDING, response.getBody().get(0).status());

        // InOrder
        InOrder inOrder = inOrder(listInstallmentsUseCase);
        inOrder.verify(listInstallmentsUseCase).list(any(), any(), any(), any(), any(), anyBoolean());
    }

    @Test
    void should_list_installments_filtered_by_status() {
        // WHEN
        when(listInstallmentsUseCase.list(any(), any(), any(), any(), any(), anyBoolean())).thenReturn(List.of(summary));

        // ASSERT
        var response = controller.list(InstallmentStatus.PENDING, null, null, null, null, false);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(InstallmentStatus.PENDING, response.getBody().get(0).status());

        // InOrder
        InOrder inOrder = inOrder(listInstallmentsUseCase);
        inOrder.verify(listInstallmentsUseCase).list(any(), any(), any(), any(), any(), anyBoolean());
    }

    @Test
    void should_list_all_installments_when_show_all_is_true() {
        // WHEN
        when(listInstallmentsUseCase.list(any(), any(), any(), any(), any(), eq(true))).thenReturn(List.of(summary));

        // ASSERT
        var response = controller.list(null, null, null, null, null, true);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());

        // InOrder
        InOrder inOrder = inOrder(listInstallmentsUseCase);
        inOrder.verify(listInstallmentsUseCase).list(any(), any(), any(), any(), any(), eq(true));
    }

    @Test
    void should_return_installment_detail_with_200() {
        // WHEN
        when(findInstallmentByIdUseCase.findById(1L)).thenReturn(detail);

        // ASSERT
        var response = controller.findById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
        assertEquals(1, response.getBody().installmentNumber());
        assertEquals(3, response.getBody().totalInstallments());
        assertEquals("PED-001", response.getBody().orderNumber());
        assertEquals("João Silva", response.getBody().clientName());
        assertEquals(InstallmentStatus.PENDING, response.getBody().status());
        assertEquals(null, response.getBody().paymentDate());

        // InOrder
        InOrder inOrder = inOrder(findInstallmentByIdUseCase);
        inOrder.verify(findInstallmentByIdUseCase).findById(1L);
    }

    @Test
    void should_list_installments_by_order() {
        // WHEN
        when(listInstallmentsByOrderUseCase.listByOrder(1L)).thenReturn(List.of(summary));

        // ASSERT
        var response = controller.listByOrder(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("PED-001", response.getBody().get(0).orderNumber());

        // InOrder
        InOrder inOrder = inOrder(listInstallmentsByOrderUseCase);
        inOrder.verify(listInstallmentsByOrderUseCase).listByOrder(1L);
    }

    @Test
    void should_update_status_to_paid_and_return_payment_date() {
        var paidDetail = new InstallmentResponse(1L, 1, 3, new BigDecimal("100.00"),
                LocalDate.of(2026, 2, 15), InstallmentStatus.PAID, LocalDate.now(),
                1L, "PED-001", "João Silva", "Maria Souza");

        // WHEN
        when(updateInstallmentStatusUseCase.updateStatus(any(Long.class), any(UpdateInstallmentStatusRequest.class))).thenReturn(paidDetail);

        // ASSERT
        var response = controller.updateStatus(1L, new UpdateInstallmentStatusRequest(InstallmentStatus.PAID));

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(InstallmentStatus.PAID, response.getBody().status());
        assertEquals(LocalDate.now(), response.getBody().paymentDate());

        // InOrder
        InOrder inOrder = inOrder(updateInstallmentStatusUseCase);
        inOrder.verify(updateInstallmentStatusUseCase).updateStatus(any(Long.class), any(UpdateInstallmentStatusRequest.class));
    }

    @Test
    void should_update_status_to_overdue_without_payment_date() {
        var overdueDetail = new InstallmentResponse(1L, 1, 3, new BigDecimal("100.00"),
                LocalDate.of(2026, 2, 15), InstallmentStatus.OVERDUE, null,
                1L, "PED-001", "João Silva", "Maria Souza");

        // WHEN
        when(updateInstallmentStatusUseCase.updateStatus(any(Long.class), any(UpdateInstallmentStatusRequest.class))).thenReturn(overdueDetail);

        // ASSERT
        var response = controller.updateStatus(1L, new UpdateInstallmentStatusRequest(InstallmentStatus.OVERDUE));

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(InstallmentStatus.OVERDUE, response.getBody().status());
        assertEquals(null, response.getBody().paymentDate());

        // InOrder
        InOrder inOrder = inOrder(updateInstallmentStatusUseCase);
        inOrder.verify(updateInstallmentStatusUseCase).updateStatus(any(Long.class), any(UpdateInstallmentStatusRequest.class));
    }
}
