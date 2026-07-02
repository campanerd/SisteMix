package org.example.client;

import org.example.client.dto.CreateClientRequest;
import org.example.client.dto.UpdateClientRequest;
import org.example.client.model.Client;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClientTest {

    @Test
    void should_create_client_with_all_fields_and_active_true() {
        var request = new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com");
        var client = new Client(request);

        assertThat(client.getName()).isEqualTo("João Silva");
        assertThat(client.getPhone()).isEqualTo("11999999999");
        assertThat(client.getCpfCnpj()).isEqualTo("12345678900");
        assertThat(client.getEmail()).isEqualTo("joao@email.com");
        assertThat(client.getActive()).isTrue();
    }

    @Test
    void should_update_only_provided_fields() {
        var client = new Client(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));

        client.update(new UpdateClientRequest(null, "José Santos", null, null));

        assertThat(client.getName()).isEqualTo("José Santos");
        assertThat(client.getPhone()).isEqualTo("11999999999");
        assertThat(client.getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    void should_keep_all_fields_when_update_is_null() {
        var client = new Client(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));

        client.update(new UpdateClientRequest(null, null, null, null));

        assertThat(client.getName()).isEqualTo("João Silva");
        assertThat(client.getPhone()).isEqualTo("11999999999");
        assertThat(client.getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    void should_set_active_to_false_when_deactivated() {
        var client = new Client(new CreateClientRequest("João Silva", null, null, null));

        assertThat(client.getActive()).isTrue();
        client.deactivate();
        assertThat(client.getActive()).isFalse();
    }
}
