package org.siste.mix.installment.model;

import org.siste.mix.client.dto.CreateClientRequest;
import org.siste.mix.client.model.Client;
import org.siste.mix.installment.enums.InstallmentStatus;
import org.siste.mix.order.dto.CreateOrderRequest;
import org.siste.mix.order.model.Order;
import org.siste.mix.seller.dto.CreateSellerRequest;
import org.siste.mix.seller.model.Seller;
import org.siste.mix.user.dto.CreateUserRequest;
import org.siste.mix.user.enums.UserRole;
import org.siste.mix.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class InstallmentTest {

    private Order order;

    @BeforeEach
    void setUp() {
        var client = new Client(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        var seller = new Seller(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888"));
        var request = new CreateOrderRequest("PED-001",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, 1L, 1L);
        var createdBy = new User(new CreateUserRequest("Ana Admin", "ana@email.com", "123456", UserRole.ROLE_ADMIN), "hash");
        order = new Order(request, client, seller, createdBy);
    }

    @Test
    void should_create_installment_with_pending_status_and_null_payment_date() {
        var installment = new Installment(1, new BigDecimal("100.00"), LocalDate.of(2026, 2, 15), order);

        assertThat(installment.getStatus()).isEqualTo(InstallmentStatus.PENDING);
        assertThat(installment.getPaymentDate()).isNull();
        assertThat(installment.getAmount()).isEqualByComparingTo("100.00");
        assertThat(installment.getInstallmentNumber()).isEqualTo(1);
    }

    @Test
    void should_set_payment_date_when_marked_as_paid() {
        var installment = new Installment(1, new BigDecimal("100.00"), LocalDate.of(2026, 2, 15), order);

        installment.updateStatus(InstallmentStatus.PAID);

        assertThat(installment.getStatus()).isEqualTo(InstallmentStatus.PAID);
        assertThat(installment.getPaymentDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void should_clear_payment_date_when_reverted_to_pending() {
        var installment = new Installment(1, new BigDecimal("100.00"), LocalDate.of(2026, 2, 15), order);
        installment.updateStatus(InstallmentStatus.PAID);

        installment.updateStatus(InstallmentStatus.PENDING);

        assertThat(installment.getStatus()).isEqualTo(InstallmentStatus.PENDING);
        assertThat(installment.getPaymentDate()).isNull();
    }

    @Test
    void should_clear_payment_date_when_marked_as_overdue() {
        var installment = new Installment(1, new BigDecimal("100.00"), LocalDate.of(2026, 2, 15), order);
        installment.updateStatus(InstallmentStatus.PAID);

        installment.updateStatus(InstallmentStatus.OVERDUE);

        assertThat(installment.getStatus()).isEqualTo(InstallmentStatus.OVERDUE);
        assertThat(installment.getPaymentDate()).isNull();
    }
}
