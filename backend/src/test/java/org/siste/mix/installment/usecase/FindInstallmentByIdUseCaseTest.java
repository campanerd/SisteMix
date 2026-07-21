package org.siste.mix.installment.usecase;

import jakarta.persistence.EntityNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(FindInstallmentByIdUseCase.class)
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class FindInstallmentByIdUseCaseTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private FindInstallmentByIdUseCase useCase;

    @Test
    void should_find_installment_by_id_when_order_is_active() {
        var order = persistOrder(true);
        var installment = persistInstallment(order, 1, new BigDecimal("100.00"), LocalDate.of(2026, 2, 15), InstallmentStatus.PENDING);
        em.flush();

        var result = useCase.findById(installment.getId());

        assertThat(result.id()).isEqualTo(installment.getId());
        assertThat(result.orderNumber()).isEqualTo(order.getOrderNumber());
    }

    @Test
    void should_throw_when_find_installment_by_id_of_deactivated_order() {
        var order = persistOrder(true);
        var installment = persistInstallment(order, 1, new BigDecimal("100.00"), LocalDate.of(2026, 2, 15), InstallmentStatus.PENDING);
        order.deactivate();
        em.flush();

        assertThatThrownBy(() -> useCase.findById(installment.getId()))
                .isInstanceOf(EntityNotFoundException.class);
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
