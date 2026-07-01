package org.example.controller;

import org.example.cliente.Cliente;
import org.example.cliente.CreateClientRequest;
import org.example.parcela.*;
import org.example.pedido.CreateOrderRequest;
import org.example.pedido.Pedido;
import org.example.vendedor.CreateSellerRequest;
import org.example.vendedor.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParcelaControllerTest {

    @Mock
    private ParcelaService service;

    @InjectMocks
    private ParcelaController controller;

    private Parcela parcela;

    @BeforeEach
    void setUp() {
        var cliente = new Cliente(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        var vendedor = new Vendedor(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888"));
        var pedido = new Pedido(1L, "PED-001",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, cliente, vendedor, true);
        parcela = new Parcela(1L, 1, new BigDecimal("100.00"),
                LocalDate.of(2026, 2, 15), InstallmentStatus.PENDENTE, null, pedido);
    }

    @Test
    void shouldListInstallmentsWithoutFilters() {
        var summary = new InstallmentSummary(parcela);
        when(service.list(any(), any(), any(), any(), any())).thenReturn(List.of(summary));

        var response = controller.list(null, null, null, null, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).numeroPedido()).isEqualTo("PED-001");
        assertThat(response.getBody().get(0).nomeCliente()).isEqualTo("João Silva");
        assertThat(response.getBody().get(0).status()).isEqualTo(InstallmentStatus.PENDENTE);
    }

    @Test
    void shouldListInstallmentsWithStatusFilter() {
        var summary = new InstallmentSummary(parcela);
        when(service.list(any(), any(), any(), any(), any())).thenReturn(List.of(summary));

        var response = controller.list(InstallmentStatus.PENDENTE, null, null, null, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).status()).isEqualTo(InstallmentStatus.PENDENTE);
    }

    @Test
    void shouldFindInstallmentByIdAndReturn200() {
        var installmentResponse = new InstallmentResponse(parcela);
        when(service.findById(1L)).thenReturn(installmentResponse);

        var response = controller.findById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().numeroParcela()).isEqualTo(1);
        assertThat(response.getBody().totalParcelas()).isEqualTo(3);
        assertThat(response.getBody().numeroPedido()).isEqualTo("PED-001");
        assertThat(response.getBody().nomeCliente()).isEqualTo("João Silva");
        assertThat(response.getBody().status()).isEqualTo(InstallmentStatus.PENDENTE);
        assertThat(response.getBody().dataPagamento()).isNull();
    }

    @Test
    void shouldListInstallmentsByOrder() {
        var summary = new InstallmentSummary(parcela);
        when(service.listByOrder(1L)).thenReturn(List.of(summary));

        var response = controller.listByOrder(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).numeroPedido()).isEqualTo("PED-001");
    }

    @Test
    void shouldUpdateStatusToPaidAndReturnPaymentDate() {
        var paid = new Parcela(1L, 1, new BigDecimal("100.00"),
                LocalDate.of(2026, 2, 15), InstallmentStatus.PAGO, LocalDate.now(), parcela.getPedido());
        when(service.updateStatus(any(), any())).thenReturn(new InstallmentResponse(paid));

        var response = controller.updateStatus(1L, new UpdateInstallmentStatusRequest(InstallmentStatus.PAGO));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().status()).isEqualTo(InstallmentStatus.PAGO);
        assertThat(response.getBody().dataPagamento()).isEqualTo(LocalDate.now());
    }

    @Test
    void shouldUpdateStatusToOverdueWithNullPaymentDate() {
        var overdue = new Parcela(1L, 1, new BigDecimal("100.00"),
                LocalDate.of(2026, 2, 15), InstallmentStatus.EM_ATRASO, null, parcela.getPedido());
        when(service.updateStatus(any(), any())).thenReturn(new InstallmentResponse(overdue));

        var response = controller.updateStatus(1L, new UpdateInstallmentStatusRequest(InstallmentStatus.EM_ATRASO));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().status()).isEqualTo(InstallmentStatus.EM_ATRASO);
        assertThat(response.getBody().dataPagamento()).isNull();
    }
}
