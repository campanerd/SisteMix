package org.siste.mix.installment.usecase;

import org.siste.mix.client.dto.CreateClientRequest;
import org.siste.mix.client.model.Client;
import org.siste.mix.installment.enums.InstallmentStatus;
import org.siste.mix.installment.model.Installment;
import org.siste.mix.order.dto.CreateOrderRequest;
import org.siste.mix.order.model.Order;
import org.siste.mix.seller.dto.CreateSellerRequest;
import org.siste.mix.seller.model.Seller;
import org.siste.mix.user.dto.CreateUserRequest;
import org.siste.mix.user.enums.UserRole;
import org.siste.mix.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ListInstallmentsUseCase.class)
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ListInstallmentsUseCaseTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ListInstallmentsUseCase useCase;

    @Test
    void should_return_next_due_installment_per_order_when_no_filter_applied() {
        var order = persistOrder(true);
        persistInstallment(order, 1, new BigDecimal("100.00"), LocalDate.of(2026, 2, 15), InstallmentStatus.PENDING);
        em.flush();

        var result = useCase.list(null, null, null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).orderNumber()).isEqualTo(order.getOrderNumber());
    }

    @Test
    void should_not_list_installments_of_deactivated_order() {
        var order = persistOrder(true);
        persistInstallment(order, 1, new BigDecimal("100.00"), LocalDate.of(2026, 2, 15), InstallmentStatus.PENDING);
        order.deactivate();
        em.flush();

        var result = useCase.list(null, null, null, null, null);

        assertThat(result).isEmpty();
    }

    @Test
    void should_filter_installments_by_status() {
        var order = persistOrder(true);
        persistInstallment(order, 1, new BigDecimal("100.00"), LocalDate.of(2026, 2, 15), InstallmentStatus.PENDING);
        persistInstallment(order, 2, new BigDecimal("100.00"), LocalDate.of(2026, 3, 15), InstallmentStatus.PAID);
        em.flush();

        var result = useCase.list(InstallmentStatus.PAID, null, null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(InstallmentStatus.PAID);
    }

    @Test
    void should_filter_installments_by_due_date_range() {
        var order = persistOrder(true);
        persistInstallment(order, 1, new BigDecimal("100.00"), LocalDate.of(2026, 2, 15), InstallmentStatus.PENDING);
        persistInstallment(order, 2, new BigDecimal("100.00"), LocalDate.of(2026, 5, 15), InstallmentStatus.PENDING);
        em.flush();

        var result = useCase.list(null, LocalDate.of(2026, 4, 1), null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).dueDate()).isEqualTo(LocalDate.of(2026, 5, 15));
    }

    private Order persistOrder(boolean active) {
        var client = em.persist(new Client(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com")));
        var seller = em.persist(new Seller(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888")));
        var createdBy = em.persist(new User(new CreateUserRequest("Ana Admin", "ana@email.com", "123456", UserRole.ROLE_ADMIN), "hash"));
        var request = new CreateOrderRequest("PED-" + System.nanoTime(),
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, null, null);
        var order = new Order(request, client, seller, createdBy);
        if (!active) {
            order.deactivate();
        }
        return em.persist(order);
    }

    private Installment persistInstallment(Order order, int number, BigDecimal amount, LocalDate dueDate, InstallmentStatus status) {
        var installment = new Installment(number, amount, dueDate, order);
        if (status != InstallmentStatus.PENDING) {
            installment.updateStatus(status);
        }
        return em.persist(installment);
    }
}
