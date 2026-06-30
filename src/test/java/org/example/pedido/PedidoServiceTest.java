package org.example.pedido;

import org.example.cliente.Cliente;
import org.example.cliente.ClienteRepository;
import org.example.cliente.DadosCadastroCliente;
import org.example.parcela.Parcela;
import org.example.parcela.ParcelaRepository;
import org.example.vendedor.DadosCadastroVendedor;
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
        cliente = new Cliente(new DadosCadastroCliente("João Silva", "11999999999", "12345678900", "joao@email.com"));
        vendedor = new Vendedor(new DadosCadastroVendedor("Maria Souza", "98765432100", "11988888888"));
        when(clienteRepository.getReferenceById(1L)).thenReturn(cliente);
        when(vendedorRepository.getReferenceById(1L)).thenReturn(vendedor);
    }

    @Test
    void deveriaCriarNumeroCertoDeParcelas() {
        var dados = dadosCom(new BigDecimal("300.00"), 3, LocalDate.of(2026, 1, 15));
        var pedido = pedidoSalvo(dados);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        service.cadastrar(dados);

        verify(parcelaRepository, times(3)).save(any(Parcela.class));
    }

    @Test
    void deveriaDistribuirValorComArredondamentoNaUltimaParcela() {
        // R$100,00 / 3 = 33,33 + 33,33 + 33,34
        var dados = dadosCom(new BigDecimal("100.00"), 3, LocalDate.of(2026, 1, 15));
        var pedido = pedidoSalvo(dados);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        service.cadastrar(dados);

        var captor = ArgumentCaptor.forClass(Parcela.class);
        verify(parcelaRepository, times(3)).save(captor.capture());
        List<Parcela> parcelas = captor.getAllValues();

        assertThat(parcelas.get(0).getValor()).isEqualByComparingTo("33.33");
        assertThat(parcelas.get(1).getValor()).isEqualByComparingTo("33.33");
        assertThat(parcelas.get(2).getValor()).isEqualByComparingTo("33.34");
    }

    @Test
    void deveriaDistribuirValorIgualQuandoDivisaoExata() {
        // R$300,00 / 3 = 100,00 cada
        var dados = dadosCom(new BigDecimal("300.00"), 3, LocalDate.of(2026, 1, 15));
        var pedido = pedidoSalvo(dados);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        service.cadastrar(dados);

        var captor = ArgumentCaptor.forClass(Parcela.class);
        verify(parcelaRepository, times(3)).save(captor.capture());
        List<Parcela> parcelas = captor.getAllValues();

        assertThat(parcelas).allSatisfy(p ->
                assertThat(p.getValor()).isEqualByComparingTo("100.00")
        );
    }

    @Test
    void deveriaDefinirVencimentosMensaisAPartirDaDataDoPedido() {
        var dataPedido = LocalDate.of(2026, 1, 15);
        var dados = dadosCom(new BigDecimal("200.00"), 2, dataPedido);
        var pedido = pedidoSalvo(dados);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        service.cadastrar(dados);

        var captor = ArgumentCaptor.forClass(Parcela.class);
        verify(parcelaRepository, times(2)).save(captor.capture());
        List<Parcela> parcelas = captor.getAllValues();

        assertThat(parcelas.get(0).getVencimento()).isEqualTo(LocalDate.of(2026, 2, 15));
        assertThat(parcelas.get(1).getVencimento()).isEqualTo(LocalDate.of(2026, 3, 15));
    }

    private DadosCadastroPedido dadosCom(BigDecimal valor, int totalParcelas, LocalDate dataPedido) {
        return new DadosCadastroPedido(
                "PED-001",
                dataPedido,
                dataPedido,
                valor,
                totalParcelas,
                null,
                1L, 1L
        );
    }

    private Pedido pedidoSalvo(DadosCadastroPedido dados) {
        return new Pedido(1L, dados.numeroPedido(), dados.dataEmissao(), dados.dataPedido(),
                dados.valorTotal(), dados.totalParcelas(), dados.observacao(), cliente, vendedor, true);
    }
}
