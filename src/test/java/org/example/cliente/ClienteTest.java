package org.example.cliente;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClienteTest {

    @Test
    void deveriaCriarClienteComTodosOsCamposEAtivoTrue() {
        var dados = new DadosCadastroCliente("João Silva", "11999999999", "12345678900", "joao@email.com");
        var cliente = new Cliente(dados);

        assertThat(cliente.getNome()).isEqualTo("João Silva");
        assertThat(cliente.getTelefone()).isEqualTo("11999999999");
        assertThat(cliente.getCpfCnpj()).isEqualTo("12345678900");
        assertThat(cliente.getEmail()).isEqualTo("joao@email.com");
        assertThat(cliente.getAtivo()).isTrue();
    }

    @Test
    void deveriaAtualizarSomenteOsCamposInformados() {
        var dados = new DadosCadastroCliente("João Silva", "11999999999", "12345678900", "joao@email.com");
        var cliente = new Cliente(dados);

        var atualizacao = new DadosAtualizacaoCliente(null, "José Santos", null, null);
        cliente.atualizarInformacoes(atualizacao);

        assertThat(cliente.getNome()).isEqualTo("José Santos");
        assertThat(cliente.getTelefone()).isEqualTo("11999999999");
        assertThat(cliente.getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    void deveriaManterTodosOsCamposQuandoAtualizacaoForNula() {
        var dados = new DadosCadastroCliente("João Silva", "11999999999", "12345678900", "joao@email.com");
        var cliente = new Cliente(dados);

        var atualizacao = new DadosAtualizacaoCliente(null, null, null, null);
        cliente.atualizarInformacoes(atualizacao);

        assertThat(cliente.getNome()).isEqualTo("João Silva");
        assertThat(cliente.getTelefone()).isEqualTo("11999999999");
        assertThat(cliente.getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    void deveriaDefinirAtivoFalsoAoExcluir() {
        var dados = new DadosCadastroCliente("João Silva", null, null, null);
        var cliente = new Cliente(dados);

        assertThat(cliente.getAtivo()).isTrue();
        cliente.excluir();
        assertThat(cliente.getAtivo()).isFalse();
    }
}
