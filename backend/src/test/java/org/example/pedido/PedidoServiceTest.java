package org.example.pedido;

import org.example.cliente.Cliente;
import org.example.cliente.ClienteRepository;
import org.example.cliente.CreateClientRequest;
import org.example.parcela.Parcela;
import org.example.parcela.ParcelaRepository;
import org.example.vendedor.CreateSellerRequest;
import org.example.vendedor.Vendedor;
import org.example.vendedor.VendedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private ParcelaRepository parcelaRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private VendedorRepository vendedorRepository;

    @InjectMocks
    private PedidoService service;

    private Cliente cliente;
    private Vendedor vendedor;

    @BeforeEach
    void setUp() {
        cliente = new Cliente(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        vendedor = new Vendedor(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888"));
        when(clienteRepository.getReferenceById(1L)).thenReturn(cliente);
        when(vendedorRepository.getReferenceById(1L)).thenReturn(vendedor);
    }

    @Test
    void shouldCreateCorrectNumberOfInstallments() {
        var data = orderWith(new BigDecimal("300.00"), 3, LocalDate.of(2026, 1, 15));
        var pedido = savedOrder(data);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        service.create(data);

        verify(parcelaRepository, times(3)).save(any(Parcela.class));
    }

    @Test
    void shouldDistributeValueWithRoundingOnLastInstallment() {
        // R$100,00 / 3 = 33,33 + 33,33 + 33,34
        var data = orderWith(new BigDecimal("100.00"), 3, LocalDate.of(2026, 1, 15));
        var pedido = savedOrder(data);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        service.create(data);

        var captor = ArgumentCaptor.forClass(Parcela.class);
        verify(parcelaRepository, times(3)).save(captor.capture());
        List<Parcela> parcelas = captor.getAllValues();

        assertThat(parcelas.get(0).getValor()).isEqualByComparingTo("33.33");
        assertThat(parcelas.get(1).getValor()).isEqualByComparingTo("33.33");
        assertThat(parcelas.get(2).getValor()).isEqualByComparingTo("33.34");
    }

    @Test
    void shouldDistributeEquallyWhenDivisionIsExact() {
        // R$300,00 / 3 = 100,00 each
        var data = orderWith(new BigDecimal("300.00"), 3, LocalDate.of(2026, 1, 15));
        var pedido = savedOrder(data);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        service.create(data);

        var captor = ArgumentCaptor.forClass(Parcela.class);
        verify(parcelaRepository, times(3)).save(captor.capture());
        List<Parcela> parcelas = captor.getAllValues();

        assertThat(parcelas).allSatisfy(p ->
                assertThat(p.getValor()).isEqualByComparingTo("100.00")
        );
    }

    @Test
    void shouldSetMonthlyDueDatesFromOrderDate() {
        var orderDate = LocalDate.of(2026, 1, 15);
        var data = orderWith(new BigDecimal("200.00"), 2, orderDate);
        var pedido = savedOrder(data);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        service.create(data);

        var captor = ArgumentCaptor.forClass(Parcela.class);
        verify(parcelaRepository, times(2)).save(captor.capture());
        List<Parcela> parcelas = captor.getAllValues();

        assertThat(parcelas.get(0).getVencimento()).isEqualTo(LocalDate.of(2026, 2, 15));
        assertThat(parcelas.get(1).getVencimento()).isEqualTo(LocalDate.of(2026, 3, 15));
    }

    private CreateOrderRequest orderWith(BigDecimal valor, int totalParcelas, LocalDate dataPedido) {
        return new CreateOrderRequest(
                "PED-001",
                dataPedido,
                dataPedido,
                valor,
                totalParcelas,
                null,
                1L, 1L
        );
    }

    private Pedido savedOrder(CreateOrderRequest data) {
        return new Pedido(1L, data.numeroPedido(), data.dataEmissao(), data.dataPedido(),
                data.valorTotal(), data.totalParcelas(), data.observacao(), cliente, vendedor, true);
    }
}
