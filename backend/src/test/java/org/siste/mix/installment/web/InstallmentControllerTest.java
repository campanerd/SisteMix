package org.siste.mix.installment.web;

import org.siste.mix.client.dto.CreateClientRequest;
import org.siste.mix.client.model.Client;
import org.siste.mix.installment.dto.InstallmentResponse;
import org.siste.mix.installment.dto.InstallmentSummary;
import org.siste.mix.installment.dto.UpdateInstallmentStatusRequest;
import org.siste.mix.installment.enums.InstallmentStatus;
import org.siste.mix.installment.model.Installment;
import org.siste.mix.installment.service.InstallmentService;
import org.siste.mix.order.model.Order;
import org.siste.mix.seller.dto.CreateSellerRequest;
import org.siste.mix.seller.model.Seller;
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
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallmentControllerTest {

    @Mock
    private InstallmentService service;

    @InjectMocks
    private InstallmentController controller;

    private InstallmentSummary summary;
    private InstallmentResponse detail;

    @BeforeEach
    void setUp() {
        var client = new Client(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        var seller = new Seller(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888"));
        var order = new Order(1L, "PED-001",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, client, seller, true);
        var installment = new Installment(1L, 1, new BigDecimal("100.00"),
                LocalDate.of(2026, 2, 15), InstallmentStatus.PENDING, null, order);
        summary = new InstallmentSummary(installment);
        detail = new InstallmentResponse(installment);
    }

    @Test
    void should_list_installments_without_filters() {
        // WHEN
        when(service.list(any(), any(), any(), any(), any())).thenReturn(List.of(summary));

        // ASSERT
        var response = controller.list(null, null, null, null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("PED-001", response.getBody().get(0).orderNumber());
        assertEquals("João Silva", response.getBody().get(0).clientName());
        assertEquals(InstallmentStatus.PENDING, response.getBody().get(0).status());

        // InOrder
        InOrder inOrder = inOrder(service);
        inOrder.verify(service).list(any(), any(), any(), any(), any());
    }

    @Test
    void should_list_installments_filtered_by_status() {
        // WHEN
        when(service.list(any(), any(), any(), any(), any())).thenReturn(List.of(summary));

        // ASSERT
        var response = controller.list(InstallmentStatus.PENDING, null, null, null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(InstallmentStatus.PENDING, response.getBody().get(0).status());

        // InOrder
        InOrder inOrder = inOrder(service);
        inOrder.verify(service).list(any(), any(), any(), any(), any());
    }

    @Test
    void should_return_installment_detail_with_200() {
        // WHEN
        when(service.findById(1L)).thenReturn(detail);

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
        InOrder inOrder = inOrder(service);
        inOrder.verify(service).findById(1L);
    }

    @Test
    void should_list_installments_by_order() {
        // WHEN
        when(service.listByOrder(1L)).thenReturn(List.of(summary));

        // ASSERT
        var response = controller.listByOrder(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("PED-001", response.getBody().get(0).orderNumber());

        // InOrder
        InOrder inOrder = inOrder(service);
        inOrder.verify(service).listByOrder(1L);
    }

    @Test
    void should_update_status_to_paid_and_return_payment_date() {
        var paidDetail = new InstallmentResponse(1L, 1, 3, new BigDecimal("100.00"),
                LocalDate.of(2026, 2, 15), InstallmentStatus.PAID, LocalDate.now(),
                1L, "PED-001", "João Silva", "Maria Souza");

        // WHEN
        when(service.updateStatus(any(Long.class), any(UpdateInstallmentStatusRequest.class))).thenReturn(paidDetail);

        // ASSERT
        var response = controller.updateStatus(1L, new UpdateInstallmentStatusRequest(InstallmentStatus.PAID));

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(InstallmentStatus.PAID, response.getBody().status());
        assertEquals(LocalDate.now(), response.getBody().paymentDate());

        // InOrder
        InOrder inOrder = inOrder(service);
        inOrder.verify(service).updateStatus(any(Long.class), any(UpdateInstallmentStatusRequest.class));
    }

    @Test
    void should_update_status_to_overdue_without_payment_date() {
        var overdueDetail = new InstallmentResponse(1L, 1, 3, new BigDecimal("100.00"),
                LocalDate.of(2026, 2, 15), InstallmentStatus.OVERDUE, null,
                1L, "PED-001", "João Silva", "Maria Souza");

        // WHEN
        when(service.updateStatus(any(Long.class), any(UpdateInstallmentStatusRequest.class))).thenReturn(overdueDetail);

        // ASSERT
        var response = controller.updateStatus(1L, new UpdateInstallmentStatusRequest(InstallmentStatus.OVERDUE));

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(InstallmentStatus.OVERDUE, response.getBody().status());
        assertEquals(null, response.getBody().paymentDate());

        // InOrder
        InOrder inOrder = inOrder(service);
        inOrder.verify(service).updateStatus(any(Long.class), any(UpdateInstallmentStatusRequest.class));
    }
}
