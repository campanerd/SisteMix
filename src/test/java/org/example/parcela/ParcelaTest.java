package org.example.parcela;

import org.example.cliente.Cliente;
import org.example.cliente.DadosCadastroCliente;
import org.example.pedido.DadosCadastroPedido;
import org.example.pedido.Pedido;
import org.example.vendedor.DadosCadastroVendedor;
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
        var cliente = new Cliente(new DadosCadastroCliente("João Silva", "11999999999", "12345678900", "joao@email.com"));
        var vendedor = new Vendedor(new DadosCadastroVendedor("Maria Souza", "98765432100", "11988888888"));
        var dados = new DadosCadastroPedido("PED-001",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, 1L, 1L);
        pedido = new Pedido(dados, cliente, vendedor);
    }

    @Test
    void deveriaCriarParcelaComStatusPendenteEDataPagamentoNula() {
        var parcela = new Parcela(1, new BigDecimal("100.00"), LocalDate.of(2026, 2, 15), pedido);

        assertThat(parcela.getStatus()).isEqualTo(StatusParcela.PENDENTE);
        assertThat(parcela.getDataPagamento()).isNull();
        assertThat(parcela.getValor()).isEqualByComparingTo("100.00");
        assertThat(parcela.getNumeroParcela()).isEqualTo(1);
    }

    @Test
    void deveriaDefinirDataPagamentoAoMarcarComoPago() {
        var parcela = new Parcela(1, new BigDecimal("100.00"), LocalDate.of(2026, 2, 15), pedido);

        parcela.atualizarStatus(StatusParcela.PAGO);

        assertThat(parcela.getStatus()).isEqualTo(StatusParcela.PAGO);
        assertThat(parcela.getDataPagamento()).isEqualTo(LocalDate.now());
    }

    @Test
    void deveriaLimparDataPagamentoAoVoltarParaPendente() {
        var parcela = new Parcela(1, new BigDecimal("100.00"), LocalDate.of(2026, 2, 15), pedido);
        parcela.atualizarStatus(StatusParcela.PAGO);

        parcela.atualizarStatus(StatusParcela.PENDENTE);

        assertThat(parcela.getStatus()).isEqualTo(StatusParcela.PENDENTE);
        assertThat(parcela.getDataPagamento()).isNull();
    }

    @Test
    void deveriaLimparDataPagamentoAoMarcarComoEmAtraso() {
        var parcela = new Parcela(1, new BigDecimal("100.00"), LocalDate.of(2026, 2, 15), pedido);
        parcela.atualizarStatus(StatusParcela.PAGO);

        parcela.atualizarStatus(StatusParcela.EM_ATRASO);

        assertThat(parcela.getStatus()).isEqualTo(StatusParcela.EM_ATRASO);
        assertThat(parcela.getDataPagamento()).isNull();
    }
}
