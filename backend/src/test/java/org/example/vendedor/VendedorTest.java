package org.example.vendedor;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VendedorTest {

    @Test
    void deveriaCriarVendedorComTodosOsCamposEAtivoTrue() {
        var dados = new DadosCadastroVendedor("Maria Souza", "12345678900", "11988888888");
        var vendedor = new Vendedor(dados);

        assertThat(vendedor.getNome()).isEqualTo("Maria Souza");
        assertThat(vendedor.getCpf()).isEqualTo("12345678900");
        assertThat(vendedor.getTelefone()).isEqualTo("11988888888");
        assertThat(vendedor.getAtivo()).isTrue();
    }

    @Test
    void deveriaAtualizarSomenteOsCamposInformados() {
        var dados = new DadosCadastroVendedor("Maria Souza", "12345678900", "11988888888");
        var vendedor = new Vendedor(dados);

        var atualizacao = new DadosAtualizacaoVendedor(null, "Maria Santos", null);
        vendedor.atualizarInformacoes(atualizacao);

        assertThat(vendedor.getNome()).isEqualTo("Maria Santos");
        assertThat(vendedor.getTelefone()).isEqualTo("11988888888");
    }

    @Test
    void deveriaManterTodosOsCamposQuandoAtualizacaoForNula() {
        var dados = new DadosCadastroVendedor("Maria Souza", "12345678900", "11988888888");
        var vendedor = new Vendedor(dados);

        var atualizacao = new DadosAtualizacaoVendedor(null, null, null);
        vendedor.atualizarInformacoes(atualizacao);

        assertThat(vendedor.getNome()).isEqualTo("Maria Souza");
        assertThat(vendedor.getTelefone()).isEqualTo("11988888888");
    }

    @Test
    void deveriaDefinirAtivoFalsoAoExcluir() {
        var dados = new DadosCadastroVendedor("Maria Souza", null, null);
        var vendedor = new Vendedor(dados);

        assertThat(vendedor.getAtivo()).isTrue();
        vendedor.excluir();
        assertThat(vendedor.getAtivo()).isFalse();
    }
}
