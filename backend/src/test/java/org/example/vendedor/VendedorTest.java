package org.example.vendedor;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VendedorTest {

    @Test
    void shouldCreateSellerWithAllFieldsAndAtivoTrue() {
        var data = new CreateSellerRequest("Maria Souza", "12345678900", "11988888888");
        var vendedor = new Vendedor(data);

        assertThat(vendedor.getNome()).isEqualTo("Maria Souza");
        assertThat(vendedor.getCpf()).isEqualTo("12345678900");
        assertThat(vendedor.getTelefone()).isEqualTo("11988888888");
        assertThat(vendedor.getAtivo()).isTrue();
    }

    @Test
    void shouldUpdateOnlyProvidedFields() {
        var data = new CreateSellerRequest("Maria Souza", "12345678900", "11988888888");
        var vendedor = new Vendedor(data);

        var updateData = new UpdateSellerRequest(null, "Maria Santos", null);
        vendedor.update(updateData);

        assertThat(vendedor.getNome()).isEqualTo("Maria Santos");
        assertThat(vendedor.getTelefone()).isEqualTo("11988888888");
    }

    @Test
    void shouldKeepAllFieldsWhenUpdateDataIsNull() {
        var data = new CreateSellerRequest("Maria Souza", "12345678900", "11988888888");
        var vendedor = new Vendedor(data);

        var updateData = new UpdateSellerRequest(null, null, null);
        vendedor.update(updateData);

        assertThat(vendedor.getNome()).isEqualTo("Maria Souza");
        assertThat(vendedor.getTelefone()).isEqualTo("11988888888");
    }

    @Test
    void shouldSetAtivoFalseWhenDeactivated() {
        var data = new CreateSellerRequest("Maria Souza", null, null);
        var vendedor = new Vendedor(data);

        assertThat(vendedor.getAtivo()).isTrue();
        vendedor.deactivate();
        assertThat(vendedor.getAtivo()).isFalse();
    }
}
