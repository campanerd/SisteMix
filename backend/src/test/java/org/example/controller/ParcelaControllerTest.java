package org.example.controller;

import org.example.cliente.dto.CreateClientRequest;
import org.example.cliente.model.Client;
import org.example.parcela.dto.DadosAtualizacaoParcela;
import org.example.parcela.dto.DadosDetalhamentoParcela;
import org.example.parcela.dto.DadosListagemParcela;
import org.example.parcela.enums.StatusParcela;
import org.example.parcela.model.Parcela;
import org.example.parcela.service.ParcelaService;
import org.example.parcela.web.ParcelaController;
import org.example.pedido.model.Pedido;
import org.example.vendedor.dto.CreateSellerRequest;
import org.example.vendedor.model.Seller;
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

    private DadosListagemParcela listagem;
    private DadosDetalhamentoParcela detalhamento;

    @BeforeEach
    void setUp() {
        var client = new Client(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        var seller = new Seller(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888"));
        var pedido = new Pedido(1L, "PED-001",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, client, seller, true);
        var parcela = new Parcela(1L, 1, new BigDecimal("100.00"),
                LocalDate.of(2026, 2, 15), StatusParcela.PENDENTE, null, pedido);
        listagem = new DadosListagemParcela(parcela);
        detalhamento = new DadosDetalhamentoParcela(parcela);
    }

    @Test
    void deveriaListarParcelasSemFiltros() {
        when(service.list(any(), any(), any(), any(), any())).thenReturn(List.of(listagem));

        var response = controller.list(null, null, null, null, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).numeroPedido()).isEqualTo("PED-001");
        assertThat(response.getBody().get(0).nomeCliente()).isEqualTo("João Silva");
        assertThat(response.getBody().get(0).status()).isEqualTo(StatusParcela.PENDENTE);
    }

    @Test
    void deveriaListarParcelasComFiltroDeStatus() {
        when(service.list(any(), any(), any(), any(), any())).thenReturn(List.of(listagem));

        var response = controller.list(StatusParcela.PENDENTE, null, null, null, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).status()).isEqualTo(StatusParcela.PENDENTE);
    }

    @Test
    void deveriaDetalharParcelaERetornar200() {
        when(service.findById(1L)).thenReturn(detalhamento);

        var response = controller.findById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().numeroParcela()).isEqualTo(1);
        assertThat(response.getBody().totalParcelas()).isEqualTo(3);
        assertThat(response.getBody().numeroPedido()).isEqualTo("PED-001");
        assertThat(response.getBody().nomeCliente()).isEqualTo("João Silva");
        assertThat(response.getBody().status()).isEqualTo(StatusParcela.PENDENTE);
        assertThat(response.getBody().dataPagamento()).isNull();
    }

    @Test
    void deveriaListarParcelasPorPedido() {
        when(service.listByOrder(1L)).thenReturn(List.of(listagem));

        var response = controller.listByOrder(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).numeroPedido()).isEqualTo("PED-001");
    }

    @Test
    void deveriaAtualizarStatusParaPagoERetornarDataPagamento() {
        var pagoDetalhamento = new DadosDetalhamentoParcela(1L, 1, 3, new BigDecimal("100.00"),
                LocalDate.of(2026, 2, 15), StatusParcela.PAGO, LocalDate.now(),
                1L, "PED-001", "João Silva", "Maria Souza");
        when(service.updateStatus(any(Long.class), any(DadosAtualizacaoParcela.class))).thenReturn(pagoDetalhamento);

        var response = controller.updateStatus(1L, new DadosAtualizacaoParcela(StatusParcela.PAGO));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().status()).isEqualTo(StatusParcela.PAGO);
        assertThat(response.getBody().dataPagamento()).isEqualTo(LocalDate.now());
    }

    @Test
    void deveriaAtualizarStatusParaEmAtrasoSemDataPagamento() {
        var atrasadoDetalhamento = new DadosDetalhamentoParcela(1L, 1, 3, new BigDecimal("100.00"),
                LocalDate.of(2026, 2, 15), StatusParcela.EM_ATRASO, null,
                1L, "PED-001", "João Silva", "Maria Souza");
        when(service.updateStatus(any(Long.class), any(DadosAtualizacaoParcela.class))).thenReturn(atrasadoDetalhamento);

        var response = controller.updateStatus(1L, new DadosAtualizacaoParcela(StatusParcela.EM_ATRASO));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().status()).isEqualTo(StatusParcela.EM_ATRASO);
        assertThat(response.getBody().dataPagamento()).isNull();
    }
}
