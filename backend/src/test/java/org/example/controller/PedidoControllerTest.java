package org.example.controller;

import org.example.cliente.Cliente;
import org.example.pedido.*;
import org.example.vendedor.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {

    @Mock
    private PedidoService service;

    @InjectMocks
    private PedidoController controller;

    private Pedido pedido;
    private UriComponentsBuilder uriBuilder;

    @BeforeEach
    void setUp() {
        var cliente = new Cliente(1L, "João Silva", "11999999999", "12345678900", "joao@email.com", true);
        var vendedor = new Vendedor(1L, "Maria Souza", "98765432100", "11988888888", true);
        pedido = new Pedido(1L, "PED-001",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null,
                cliente, vendedor, true);
        uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");
    }

    @Test
    void shouldCreateOrderAndReturn201() {
        var data = new CreateOrderRequest("PED-001",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, 1L, 1L);
        when(service.create(any(CreateOrderRequest.class))).thenReturn(pedido);

        var response = controller.create(data, uriBuilder);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().numeroPedido()).isEqualTo("PED-001");
        assertThat(response.getBody().nomeCliente()).isEqualTo("João Silva");
    }

    @Test
    void shouldListActiveOrders() {
        var summary = new OrderSummary(pedido);
        var page = new PageImpl<>(List.of(summary));
        when(service.list(any(Pageable.class))).thenReturn(page);

        var response = controller.list(Pageable.ofSize(10));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var content = response.getBody().getContent();
        assertThat(content).hasSize(1);
        assertThat(content.get(0).numeroPedido()).isEqualTo("PED-001");
        assertThat(content.get(0).nomeCliente()).isEqualTo("João Silva");
    }

    @Test
    void shouldUpdateOrderAndReturnUpdatedData() {
        var data = new UpdateOrderRequest(1L, null, null, null, "Nova observação", null);
        var updated = new OrderResponse(pedido);
        when(service.update(any(UpdateOrderRequest.class))).thenReturn(updated);

        var response = controller.update(data);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().valorTotal()).isEqualByComparingTo("300.00");
    }

    @Test
    void shouldUpdateOrderSellerWhenProvided() {
        var novoVendedor = new Vendedor(2L, "Carlos Lima", "11122233344", "11977777777", true);
        var pedidoAtualizado = new Pedido(1L, "PED-001",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null,
                pedido.getCliente(), novoVendedor, true);
        var data = new UpdateOrderRequest(1L, null, null, null, null, 2L);
        when(service.update(any(UpdateOrderRequest.class))).thenReturn(new OrderResponse(pedidoAtualizado));

        var response = controller.update(data);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().nomeVendedor()).isEqualTo("Carlos Lima");
    }

    @Test
    void shouldDeleteOrderAndReturn204() {
        var response = controller.delete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).delete(1L);
    }

    @Test
    void shouldFindOrderByIdAndReturn200() {
        var orderResponse = new OrderResponse(pedido);
        when(service.findById(1L)).thenReturn(orderResponse);

        var response = controller.findById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().valorTotal()).isEqualByComparingTo("300.00");
        assertThat(response.getBody().totalParcelas()).isEqualTo(3);
    }
}
