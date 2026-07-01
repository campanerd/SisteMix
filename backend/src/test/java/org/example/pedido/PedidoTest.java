package org.example.pedido;

import org.example.cliente.Cliente;
import org.example.cliente.CreateClientRequest;
import org.example.vendedor.CreateSellerRequest;
import org.example.vendedor.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PedidoTest {

    private Cliente cliente;
    private Vendedor vendedor;

    @BeforeEach
    void setUp() {
        cliente = new Cliente(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        vendedor = new Vendedor(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888"));
    }

    @Test
    void shouldCreateOrderWithAllFieldsAndAtivoTrue() {
        var data = new CreateOrderRequest(
                "PED-001",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"),
                3,
                "Observação",
                1L, 1L
        );

        var pedido = new Pedido(data, cliente, vendedor);

        assertThat(pedido.getNumeroPedido()).isEqualTo("PED-001");
        assertThat(pedido.getValorTotal()).isEqualByComparingTo("300.00");
        assertThat(pedido.getTotalParcelas()).isEqualTo(3);
        assertThat(pedido.getCliente()).isEqualTo(cliente);
        assertThat(pedido.getVendedor()).isEqualTo(vendedor);
        assertThat(pedido.getAtivo()).isTrue();
    }

    @Test
    void shouldUpdateOnlyProvidedFields() {
        var data = new CreateOrderRequest(
                "PED-001", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, 1L, 1L
        );
        var pedido = new Pedido(data, cliente, vendedor);

        var updateData = new UpdateOrderRequest(1L, null, null, null, "Nova obs", null);
        pedido.update(updateData, null);

        assertThat(pedido.getObservacao()).isEqualTo("Nova obs");
        assertThat(pedido.getValorTotal()).isEqualByComparingTo("300.00");
        assertThat(pedido.getVendedor()).isEqualTo(vendedor);
    }

    @Test
    void shouldUpdateSellerWhenProvided() {
        var data = new CreateOrderRequest(
                "PED-001", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, 1L, 1L
        );
        var pedido = new Pedido(data, cliente, vendedor);
        var novoVendedor = new Vendedor(new CreateSellerRequest("Carlos Lima", "11122233344", "11977777777"));

        var updateData = new UpdateOrderRequest(1L, null, null, null, null, 2L);
        pedido.update(updateData, novoVendedor);

        assertThat(pedido.getVendedor()).isEqualTo(novoVendedor);
    }

    @Test
    void shouldSetAtivoFalseWhenDeactivated() {
        var data = new CreateOrderRequest(
                "PED-001", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, 1L, 1L
        );
        var pedido = new Pedido(data, cliente, vendedor);

        assertThat(pedido.getAtivo()).isTrue();
        pedido.deactivate();
        assertThat(pedido.getAtivo()).isFalse();
    }
}
