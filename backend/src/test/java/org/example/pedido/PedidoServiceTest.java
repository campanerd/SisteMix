package org.example.pedido;

import org.example.cliente.dto.CreateClientRequest;
import org.example.cliente.model.Client;
import org.example.cliente.repository.ClientRepository;
import org.example.parcela.Parcela;
import org.example.parcela.ParcelaRepository;
import org.example.pedido.dto.CreateOrderRequest;
import org.example.pedido.model.Pedido;
import org.example.pedido.repository.PedidoRepository;
import org.example.pedido.service.PedidoService;
import org.example.vendedor.dto.CreateSellerRequest;
import org.example.vendedor.model.Seller;
import org.example.vendedor.repository.SellerRepository;
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
    private ClientRepository clientRepository;
    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private PedidoService service;

    private Client client;
    private Seller seller;

    @BeforeEach
    void setUp() {
        client = new Client(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        seller = new Seller(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888"));
        when(clientRepository.getReferenceById(1L)).thenReturn(client);
        when(sellerRepository.getReferenceById(1L)).thenReturn(seller);
    }

    @Test
    void deveriaCriarNumeroCertoDeParcelas() {
        var data = orderWith(new BigDecimal("300.00"), 3, LocalDate.of(2026, 1, 15));
        var pedido = savedOrder(data);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        service.create(data);

        verify(parcelaRepository, times(3)).save(any(Parcela.class));
    }

    @Test
    void deveriaDistribuirValorComArredondamentoNaUltimaParcela() {
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
    void deveriaDistribuirValorIgualQuandoDivisaoExata() {
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
    void deveriaDefinirVencimentosMensaisAPartirDaDataDoPedido() {
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
        return new CreateOrderRequest("PED-001", dataPedido, dataPedido, valor, totalParcelas, null, 1L, 1L);
    }

    private Pedido savedOrder(CreateOrderRequest data) {
        return new Pedido(1L, data.numeroPedido(), data.dataEmissao(), data.dataPedido(),
                data.valorTotal(), data.totalParcelas(), data.observacao(), client, seller, true);
    }
}
