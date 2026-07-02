package org.siste.mix.seller.model;

import org.siste.mix.seller.dto.CreateSellerRequest;
import org.siste.mix.seller.dto.UpdateSellerRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SellerTest {

    @Test
    void should_create_seller_with_all_fields_and_active_true() {
        var request = new CreateSellerRequest("Maria Souza", "12345678900", "11988888888");
        var seller = new Seller(request);

        assertThat(seller.getName()).isEqualTo("Maria Souza");
        assertThat(seller.getCpf()).isEqualTo("12345678900");
        assertThat(seller.getPhone()).isEqualTo("11988888888");
        assertThat(seller.getActive()).isTrue();
    }

    @Test
    void should_update_only_provided_fields() {
        var seller = new Seller(new CreateSellerRequest("Maria Souza", "12345678900", "11988888888"));

        seller.update(new UpdateSellerRequest(null, "Maria Santos", null));

        assertThat(seller.getName()).isEqualTo("Maria Santos");
        assertThat(seller.getPhone()).isEqualTo("11988888888");
    }

    @Test
    void should_keep_all_fields_when_update_is_null() {
        var seller = new Seller(new CreateSellerRequest("Maria Souza", "12345678900", "11988888888"));

        seller.update(new UpdateSellerRequest(null, null, null));

        assertThat(seller.getName()).isEqualTo("Maria Souza");
        assertThat(seller.getPhone()).isEqualTo("11988888888");
    }

    @Test
    void should_set_active_to_false_when_deactivated() {
        var seller = new Seller(new CreateSellerRequest("Maria Souza", null, null));

        assertThat(seller.getActive()).isTrue();
        seller.deactivate();
        assertThat(seller.getActive()).isFalse();
    }
}
