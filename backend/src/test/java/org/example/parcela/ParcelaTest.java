package org.example.parcela;

import org.example.cliente.Cliente;
import org.example.cliente.CreateClientRequest;
import org.example.pedido.CreateOrderRequest;
import org.example.pedido.Pedido;
import org.example.vendedor.CreateSellerRequest;
import org.example.vendedor.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ParcelaTest {

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        var cliente = new Cliente(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        var vendedor = new Vendedor(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888"));
        var data = new CreateOrderRequest("PED-001",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, 1L, 1L);
        pedido = new Pedido(data, cliente, vendedor);
    }

    @Test
    void shouldCreateInstallmentWithPendingStatusAndNullPaymentDate() {
        var parcela = new Parcela(1, new BigDecimal("100.00"), LocalDate.of(2026, 2, 15), pedido);

        assertThat(parcela.getStatus()).isEqualTo(InstallmentStatus.PENDENTE);
        assertThat(parcela.getDataPagamento()).isNull();
        assertThat(parcela.getValor()).isEqualByComparingTo("100.00");
        assertThat(parcela.getNumeroParcela()).isEqualTo(1);
    }

    @Test
    void shouldSetPaymentDateWhenMarkedAsPaid() {
        var parcela = new Parcela(1, new BigDecimal("100.00"), LocalDate.of(2026, 2, 15), pedido);

        parcela.updateStatus(InstallmentStatus.PAGO);

        assertThat(parcela.getStatus()).isEqualTo(InstallmentStatus.PAGO);
        assertThat(parcela.getDataPagamento()).isEqualTo(LocalDate.now());
    }

    @Test
    void shouldClearPaymentDateWhenRevertedToPending() {
        var parcela = new Parcela(1, new BigDecimal("100.00"), LocalDate.of(2026, 2, 15), pedido);
        parcela.updateStatus(InstallmentStatus.PAGO);

        parcela.updateStatus(InstallmentStatus.PENDENTE);

        assertThat(parcela.getStatus()).isEqualTo(InstallmentStatus.PENDENTE);
        assertThat(parcela.getDataPagamento()).isNull();
    }

    @Test
    void shouldClearPaymentDateWhenMarkedAsOverdue() {
        var parcela = new Parcela(1, new BigDecimal("100.00"), LocalDate.of(2026, 2, 15), pedido);
        parcela.updateStatus(InstallmentStatus.PAGO);

        parcela.updateStatus(InstallmentStatus.EM_ATRASO);

        assertThat(parcela.getStatus()).isEqualTo(InstallmentStatus.EM_ATRASO);
        assertThat(parcela.getDataPagamento()).isNull();
    }
}
