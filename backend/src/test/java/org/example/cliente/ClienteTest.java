package org.example.cliente;

import org.example.cliente.dto.CreateClientRequest;
import org.example.cliente.dto.UpdateClientRequest;
import org.example.cliente.model.Client;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClienteTest {

    @Test
    void deveriaCriarClienteComTodosOsCamposEAtivoTrue() {
        var dados = new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com");
        var client = new Client(dados);

        assertThat(client.getNome()).isEqualTo("João Silva");
        assertThat(client.getTelefone()).isEqualTo("11999999999");
        assertThat(client.getCpfCnpj()).isEqualTo("12345678900");
        assertThat(client.getEmail()).isEqualTo("joao@email.com");
        assertThat(client.getAtivo()).isTrue();
    }

    @Test
    void deveriaAtualizarSomenteOsCamposInformados() {
        var dados = new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com");
        var client = new Client(dados);

        var atualizacao = new UpdateClientRequest(null, "José Santos", null, null);
        client.update(atualizacao);

        assertThat(client.getNome()).isEqualTo("José Santos");
        assertThat(client.getTelefone()).isEqualTo("11999999999");
        assertThat(client.getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    void deveriaManterTodosOsCamposQuandoAtualizacaoForNula() {
        var dados = new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com");
        var client = new Client(dados);

        var atualizacao = new UpdateClientRequest(null, null, null, null);
        client.update(atualizacao);

        assertThat(client.getNome()).isEqualTo("João Silva");
        assertThat(client.getTelefone()).isEqualTo("11999999999");
        assertThat(client.getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    void deveriaDefinirAtivoFalsoAoDesativar() {
        var dados = new CreateClientRequest("João Silva", null, null, null);
        var client = new Client(dados);

        assertThat(client.getAtivo()).isTrue();
        client.deactivate();
        assertThat(client.getAtivo()).isFalse();
    }
}
