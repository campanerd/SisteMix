package org.example.controller;

import org.example.cliente.dto.CreateClientRequest;
import org.example.cliente.model.Client;
import org.example.parcela.DadosAtualizacaoParcela;
import org.example.parcela.DadosDetalhamentoParcela;
import org.example.parcela.DadosListagemParcela;
import org.example.parcela.Parcela;
import org.example.parcela.ParcelaRepository;
import org.example.parcela.StatusParcela;
import org.example.pedido.DadosCadastroPedido;
import org.example.pedido.Pedido;
import org.example.vendedor.DadosCadastroVendedor;
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
    private ParcelaRepository repository;

    @InjectMocks
    private ParcelaController controller;

    private Parcela parcela;

    @BeforeEach
    void setUp() {
        var client = new Client(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        var vendedor = new Vendedor(new DadosCadastroVendedor("Maria Souza", "98765432100", "11988888888"));
        var dadosPedido = new DadosCadastroPedido("PED-001",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, 1L, 1L);
        var pedido = new Pedido(1L, "PED-001",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, client, vendedor, true);
        parcela = new Parcela(1L, 1, new BigDecimal("100.00"),
                LocalDate.of(2026, 2, 15), StatusParcela.PENDENTE, null, pedido);
    }

    @Test
    void deveriaListarParcelasSemFiltros() {
        when(repository.findWithFilters(any(), any(), any(), any(), any())).thenReturn(List.of(parcela));

        var response = controller.listar(null, null, null, null, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        var item = response.getBody().get(0);
        assertThat(item.numeroPedido()).isEqualTo("PED-001");
        assertThat(item.nomeCliente()).isEqualTo("João Silva");
        assertThat(item.status()).isEqualTo(StatusParcela.PENDENTE);
    }

    @Test
    void deveriaListarParcelasComFiltroDeStatus() {
        when(repository.findWithFilters(any(), any(), any(), any(), any())).thenReturn(List.of(parcela));

        var response = controller.listar(StatusParcela.PENDENTE, null, null, null, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).status()).isEqualTo(StatusParcela.PENDENTE);
    }

    @Test
    void deveriaDetalharParcelaERetornar200() {
        when(repository.getReferenceById(1L)).thenReturn(parcela);

        var response = controller.detalhar(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = (DadosDetalhamentoParcela) response.getBody();
        assertThat(body.id()).isEqualTo(1L);
        assertThat(body.numeroParcela()).isEqualTo(1);
        assertThat(body.totalParcelas()).isEqualTo(3);
        assertThat(body.numeroPedido()).isEqualTo("PED-001");
        assertThat(body.nomeCliente()).isEqualTo("João Silva");
        assertThat(body.status()).isEqualTo(StatusParcela.PENDENTE);
        assertThat(body.dataPagamento()).isNull();
    }

    @Test
    void deveriaListarParcelasPorPedido() {
        when(repository.findByPedidoId(1L)).thenReturn(List.of(parcela));

        var response = controller.listarPorPedido(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).numeroPedido()).isEqualTo("PED-001");
    }

    @Test
    void deveriaAtualizarStatusParaPagoERetornarDataPagamento() {
        when(repository.getReferenceById(1L)).thenReturn(parcela);

        var response = controller.atualizarStatus(1L, new DadosAtualizacaoParcela(StatusParcela.PAGO));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = (DadosDetalhamentoParcela) response.getBody();
        assertThat(body.status()).isEqualTo(StatusParcela.PAGO);
        assertThat(body.dataPagamento()).isEqualTo(LocalDate.now());
    }

    @Test
    void deveriaAtualizarStatusParaEmAtrasoSemDataPagamento() {
        when(repository.getReferenceById(1L)).thenReturn(parcela);

        var response = controller.atualizarStatus(1L, new DadosAtualizacaoParcela(StatusParcela.EM_ATRASO));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = (DadosDetalhamentoParcela) response.getBody();
        assertThat(body.status()).isEqualTo(StatusParcela.EM_ATRASO);
        assertThat(body.dataPagamento()).isNull();
    }
}
