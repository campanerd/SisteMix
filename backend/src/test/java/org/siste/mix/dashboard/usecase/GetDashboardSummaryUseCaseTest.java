package org.siste.mix.dashboard.usecase;

import org.siste.mix.client.dto.CreateClientRequest;
import org.siste.mix.client.model.Client;
import org.siste.mix.installment.enums.InstallmentStatus;
import org.siste.mix.installment.model.Installment;
import org.siste.mix.installment.repository.InstallmentRepository;
import org.siste.mix.order.dto.CreateOrderRequest;
import org.siste.mix.order.model.Order;
import org.siste.mix.order.repository.OrderRepository;
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
@Import(GetDashboardSummaryUseCase.class)
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class GetDashboardSummaryUseCaseTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InstallmentRepository installmentRepository;

    @Autowired
    private GetDashboardSummaryUseCase useCase;

    @Test
    void should_summarize_active_orders_and_installments_by_status() {
        var order = persistOrder(true);
        persistInstallment(order, 1, new BigDecimal("100.00"), InstallmentStatus.PAID);
        persistInstallment(order, 2, new BigDecimal("50.00"), InstallmentStatus.PENDING);
        persistInstallment(order, 3, new BigDecimal("30.00"), InstallmentStatus.OVERDUE);

        var inactiveOrder = persistOrder(false);
        persistInstallment(inactiveOrder, 1, new BigDecimal("999.00"), InstallmentStatus.PENDING);
        em.flush();

        var result = useCase.get();

        // ASSERT
        assertThat(result.activeOrdersCount()).isEqualTo(1);
        assertThat(result.paidCount()).isEqualTo(1);
        assertThat(result.pendingCount()).isEqualTo(1);
        assertThat(result.overdueCount()).isEqualTo(1);
        assertThat(result.totalReceivedAmount()).isEqualByComparingTo("100.00");
        assertThat(result.totalToReceiveAmount()).isEqualByComparingTo("80.00");
    }

    @Test
    void should_return_zeroed_summary_when_there_is_no_data() {
        var result = useCase.get();

        // ASSERT
        assertThat(result.activeOrdersCount()).isZero();
        assertThat(result.paidCount()).isZero();
        assertThat(result.pendingCount()).isZero();
        assertThat(result.overdueCount()).isZero();
        assertThat(result.totalReceivedAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.totalToReceiveAmount()).isEqualByComparingTo(BigDecimal.ZERO);
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

    private Installment persistInstallment(Order order, int number, BigDecimal amount, InstallmentStatus status) {
        var installment = new Installment(number, amount, LocalDate.of(2026, 2, 15), order);
        if (status != InstallmentStatus.PENDING) {
            installment.updateStatus(status);
        }
        return em.persist(installment);
    }
}
