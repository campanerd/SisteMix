package org.example.cliente;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClienteTest {

    @Test
    void shouldCreateClientWithAllFieldsAndAtivoTrue() {
        var data = new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com");
        var cliente = new Cliente(data);

        assertThat(cliente.getNome()).isEqualTo("João Silva");
        assertThat(cliente.getTelefone()).isEqualTo("11999999999");
        assertThat(cliente.getCpfCnpj()).isEqualTo("12345678900");
        assertThat(cliente.getEmail()).isEqualTo("joao@email.com");
        assertThat(cliente.getAtivo()).isTrue();
    }

    @Test
    void shouldUpdateOnlyProvidedFields() {
        var data = new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com");
        var cliente = new Cliente(data);

        var updateData = new UpdateClientRequest(null, "José Santos", null, null);
        cliente.update(updateData);

        assertThat(cliente.getNome()).isEqualTo("José Santos");
        assertThat(cliente.getTelefone()).isEqualTo("11999999999");
        assertThat(cliente.getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    void shouldKeepAllFieldsWhenUpdateDataIsNull() {
        var data = new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com");
        var cliente = new Cliente(data);

        var updateData = new UpdateClientRequest(null, null, null, null);
        cliente.update(updateData);

        assertThat(cliente.getNome()).isEqualTo("João Silva");
        assertThat(cliente.getTelefone()).isEqualTo("11999999999");
        assertThat(cliente.getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    void shouldSetAtivoFalseWhenDeactivated() {
        var data = new CreateClientRequest("João Silva", null, null, null);
        var cliente = new Cliente(data);

        assertThat(cliente.getAtivo()).isTrue();
        cliente.deactivate();
        assertThat(cliente.getAtivo()).isFalse();
    }
}
