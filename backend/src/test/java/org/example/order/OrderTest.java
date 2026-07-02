package org.example.order;

import org.example.client.dto.CreateClientRequest;
import org.example.client.model.Client;
import org.example.order.dto.CreateOrderRequest;
import org.example.order.dto.UpdateOrderRequest;
import org.example.order.model.Order;
import org.example.seller.dto.CreateSellerRequest;
import org.example.seller.model.Seller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    private Client client;
    private Seller seller;

    @BeforeEach
    void setUp() {
        client = new Client(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        seller = new Seller(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888"));
    }

    @Test
    void should_create_order_with_all_fields_and_active_true() {
        var request = new CreateOrderRequest(
                "PED-001",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"),
                3,
                "Notes",
                1L, 1L
        );

        var order = new Order(request, client, seller);

        assertThat(order.getOrderNumber()).isEqualTo("PED-001");
        assertThat(order.getTotalAmount()).isEqualByComparingTo("300.00");
        assertThat(order.getTotalInstallments()).isEqualTo(3);
        assertThat(order.getClient()).isEqualTo(client);
        assertThat(order.getSeller()).isEqualTo(seller);
        assertThat(order.getActive()).isTrue();
    }

    @Test
    void should_update_only_provided_fields() {
        var order = new Order(
                new CreateOrderRequest("PED-001", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                        new BigDecimal("300.00"), 3, null, 1L, 1L),
                client, seller
        );

        order.update(new UpdateOrderRequest(1L, null, null, null, "New notes", null), null);

        assertThat(order.getNotes()).isEqualTo("New notes");
        assertThat(order.getTotalAmount()).isEqualByComparingTo("300.00");
        assertThat(order.getSeller()).isEqualTo(seller);
    }

    @Test
    void should_update_seller_when_provided() {
        var order = new Order(
                new CreateOrderRequest("PED-001", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                        new BigDecimal("300.00"), 3, null, 1L, 1L),
                client, seller
        );
        var newSeller = new Seller(new CreateSellerRequest("Carlos Lima", "11122233344", "11977777777"));

        order.update(new UpdateOrderRequest(1L, null, null, null, null, 2L), newSeller);

        assertThat(order.getSeller()).isEqualTo(newSeller);
    }

    @Test
    void should_set_active_to_false_when_deactivated() {
        var order = new Order(
                new CreateOrderRequest("PED-001", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                        new BigDecimal("300.00"), 3, null, 1L, 1L),
                client, seller
        );

        assertThat(order.getActive()).isTrue();
        order.deactivate();
        assertThat(order.getActive()).isFalse();
    }
}
