package org.example.pedido;

import org.example.cliente.dto.CreateClientRequest;
import org.example.cliente.model.Client;
import org.example.vendedor.dto.CreateSellerRequest;
import org.example.vendedor.model.Seller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PedidoTest {

    private Client client;
    private Seller seller;

    @BeforeEach
    void setUp() {
        client = new Client(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        seller = new Seller(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888"));
    }

    @Test
    void deveriaCriarPedidoComTodosOsCamposEAtivoTrue() {
        var dados = new DadosCadastroPedido(
                "PED-001",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"),
                3,
                "Observação",
                1L, 1L
        );

        var pedido = new Pedido(dados, client, seller);

        assertThat(pedido.getNumeroPedido()).isEqualTo("PED-001");
        assertThat(pedido.getValorTotal()).isEqualByComparingTo("300.00");
        assertThat(pedido.getTotalParcelas()).isEqualTo(3);
        assertThat(pedido.getClient()).isEqualTo(client);
        assertThat(pedido.getSeller()).isEqualTo(seller);
        assertThat(pedido.getAtivo()).isTrue();
    }

    @Test
    void deveriaAtualizarSomenteOsCamposInformados() {
        var dados = new DadosCadastroPedido(
                "PED-001", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, 1L, 1L
        );
        var pedido = new Pedido(dados, client, seller);

        var atualizacao = new DadosAtualizacaoPedido(1L, null, null, null, "Nova obs", null);
        pedido.atualizarInformacoes(atualizacao, null);

        assertThat(pedido.getObservacao()).isEqualTo("Nova obs");
        assertThat(pedido.getValorTotal()).isEqualByComparingTo("300.00");
        assertThat(pedido.getSeller()).isEqualTo(seller);
    }

    @Test
    void deveriaAtualizarVendedorQuandoInformado() {
        var dados = new DadosCadastroPedido(
                "PED-001", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, 1L, 1L
        );
        var pedido = new Pedido(dados, client, seller);
        var novoSeller = new Seller(new CreateSellerRequest("Carlos Lima", "11122233344", "11977777777"));

        var atualizacao = new DadosAtualizacaoPedido(1L, null, null, null, null, 2L);
        pedido.atualizarInformacoes(atualizacao, novoSeller);

        assertThat(pedido.getSeller()).isEqualTo(novoSeller);
    }

    @Test
    void deveriaDefinirAtivoFalsoAoExcluir() {
        var dados = new DadosCadastroPedido(
                "PED-001", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, 1L, 1L
        );
        var pedido = new Pedido(dados, client, seller);

        assertThat(pedido.getAtivo()).isTrue();
        pedido.excluir();
        assertThat(pedido.getAtivo()).isFalse();
    }
}
