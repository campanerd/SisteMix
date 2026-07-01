package org.example.vendedor;

import org.example.vendedor.dto.CreateSellerRequest;
import org.example.vendedor.dto.UpdateSellerRequest;
import org.example.vendedor.model.Seller;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VendedorTest {

    @Test
    void deveriaCriarVendedorComTodosOsCamposEAtivoTrue() {
        var dados = new CreateSellerRequest("Maria Souza", "12345678900", "11988888888");
        var seller = new Seller(dados);

        assertThat(seller.getNome()).isEqualTo("Maria Souza");
        assertThat(seller.getCpf()).isEqualTo("12345678900");
        assertThat(seller.getTelefone()).isEqualTo("11988888888");
        assertThat(seller.getAtivo()).isTrue();
    }

    @Test
    void deveriaAtualizarSomenteOsCamposInformados() {
        var dados = new CreateSellerRequest("Maria Souza", "12345678900", "11988888888");
        var seller = new Seller(dados);

        var atualizacao = new UpdateSellerRequest(null, "Maria Santos", null);
        seller.update(atualizacao);

        assertThat(seller.getNome()).isEqualTo("Maria Santos");
        assertThat(seller.getTelefone()).isEqualTo("11988888888");
    }

    @Test
    void deveriaManterTodosOsCamposQuandoAtualizacaoForNula() {
        var dados = new CreateSellerRequest("Maria Souza", "12345678900", "11988888888");
        var seller = new Seller(dados);

        var atualizacao = new UpdateSellerRequest(null, null, null);
        seller.update(atualizacao);

        assertThat(seller.getNome()).isEqualTo("Maria Souza");
        assertThat(seller.getTelefone()).isEqualTo("11988888888");
    }

    @Test
    void deveriaDefinirAtivoFalsoAoDesativar() {
        var dados = new CreateSellerRequest("Maria Souza", null, null);
        var seller = new Seller(dados);

        assertThat(seller.getAtivo()).isTrue();
        seller.deactivate();
        assertThat(seller.getAtivo()).isFalse();
    }
}
