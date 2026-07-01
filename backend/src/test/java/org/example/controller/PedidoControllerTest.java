package org.example.controller;

import org.example.cliente.model.Client;
import org.example.pedido.DadosAtualizacaoPedido;
import org.example.pedido.DadosCadastroPedido;
import org.example.pedido.DadosDetalhamentoPedido;
import org.example.pedido.Pedido;
import org.example.pedido.PedidoRepository;
import org.example.pedido.PedidoService;
import org.example.vendedor.Vendedor;
import org.example.vendedor.VendedorRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {

    @Mock
    private PedidoRepository repository;
    @Mock
    private PedidoService pedidoService;
    @Mock
    private VendedorRepository vendedorRepository;

    @InjectMocks
    private PedidoController controller;

    private Pedido pedido;
    private UriComponentsBuilder uriBuilder;

    @BeforeEach
    void setUp() {
        var client = new Client(1L, "João Silva", "11999999999", "12345678900", "joao@email.com", true);
        var vendedor = new Vendedor(1L, "Maria Souza", "98765432100", "11988888888", true);
        pedido = new Pedido(1L, "PED-001",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null,
                client, vendedor, true);
        uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");
    }

    @Test
    void deveriaCadastrarPedidoERetornar201() {
        var dados = new DadosCadastroPedido("PED-001",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, 1L, 1L);
        when(pedidoService.cadastrar(dados)).thenReturn(pedido);

        var response = controller.cadastrar(dados, uriBuilder);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var body = (DadosDetalhamentoPedido) response.getBody();
        assertThat(body.id()).isEqualTo(1L);
        assertThat(body.numeroPedido()).isEqualTo("PED-001");
        assertThat(body.nomeCliente()).isEqualTo("João Silva");
    }

    @Test
    void deveriaListarPedidosAtivos() {
        var page = new PageImpl<>(List.of(pedido));
        when(repository.findAllByAtivoTrue(any(Pageable.class))).thenReturn(page);

        var response = controller.listar(Pageable.ofSize(10));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var content = response.getBody().getContent();
        assertThat(content).hasSize(1);
        assertThat(content.get(0).numeroPedido()).isEqualTo("PED-001");
        assertThat(content.get(0).nomeCliente()).isEqualTo("João Silva");
    }

    @Test
    void deveriaAtualizarPedidoERetornarDadosAtualizados() {
        var dados = new DadosAtualizacaoPedido(1L, null, null, null, "Nova observação", null);
        when(repository.getReferenceById(1L)).thenReturn(pedido);

        var response = controller.atualizar(dados);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = (DadosDetalhamentoPedido) response.getBody();
        assertThat(body.observacao()).isEqualTo("Nova observação");
    }

    @Test
    void deveriaAtualizarVendedorDoPedido() {
        var novoVendedor = new Vendedor(2L, "Carlos Lima", "11122233344", "11977777777", true);
        var dados = new DadosAtualizacaoPedido(1L, null, null, null, null, 2L);
        when(repository.getReferenceById(1L)).thenReturn(pedido);
        when(vendedorRepository.getReferenceById(2L)).thenReturn(novoVendedor);

        var response = controller.atualizar(dados);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = (DadosDetalhamentoPedido) response.getBody();
        assertThat(body.nomeVendedor()).isEqualTo("Carlos Lima");
    }

    @Test
    void deveriaExcluirPedidoERetornar204() {
        when(repository.getReferenceById(1L)).thenReturn(pedido);

        var response = controller.excluir(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(pedido.getAtivo()).isFalse();
    }

    @Test
    void deveriaDetalharPedidoERetornar200() {
        when(repository.getReferenceById(1L)).thenReturn(pedido);

        var response = controller.detalhar(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = (DadosDetalhamentoPedido) response.getBody();
        assertThat(body.id()).isEqualTo(1L);
        assertThat(body.valorTotal()).isEqualByComparingTo("300.00");
        assertThat(body.totalParcelas()).isEqualTo(3);
    }
}
