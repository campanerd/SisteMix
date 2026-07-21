package org.siste.mix.installment.usecase;

import org.siste.mix.client.dto.CreateClientRequest;
import org.siste.mix.client.model.Client;
import org.siste.mix.installment.dto.UpdateInstallmentStatusRequest;
import org.siste.mix.installment.enums.InstallmentStatus;
import org.siste.mix.installment.model.Installment;
import org.siste.mix.installment.repository.InstallmentRepository;
import org.siste.mix.order.dto.CreateOrderRequest;
import org.siste.mix.order.model.Order;
import org.siste.mix.seller.dto.CreateSellerRequest;
import org.siste.mix.seller.model.Seller;
import org.siste.mix.user.dto.CreateUserRequest;
import org.siste.mix.user.enums.UserRole;
import org.siste.mix.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateInstallmentStatusUseCaseTest {

    @Mock
    private InstallmentRepository repository;

    @InjectMocks
    private UpdateInstallmentStatusUseCase useCase;

    private Installment installment() {
        var client = new Client(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        var seller = new Seller(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888"));
        var createdBy = new User(new CreateUserRequest("Ana Admin", "ana@email.com", "123456", UserRole.ROLE_ADMIN), "hash");
        var request = new CreateOrderRequest("PED-001", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15),
                new BigDecimal("300.00"), 3, null, 1L, 1L);
        var order = new Order(request, client, seller, createdBy);
        return new Installment(1, new BigDecimal("100.00"), LocalDate.of(2026, 2, 15), order);
    }

    @Test
    void should_set_payment_date_when_marked_as_paid() {
        var installment = installment();

        // WHEN
        when(repository.getReferenceById(1L)).thenReturn(installment);

        // ASSERT
        var response = useCase.updateStatus(1L, new UpdateInstallmentStatusRequest(InstallmentStatus.PAID));

        assertThat(response.status()).isEqualTo(InstallmentStatus.PAID);
        assertThat(response.paymentDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void should_clear_payment_date_when_marked_as_overdue() {
        var installment = installment();
        installment.updateStatus(InstallmentStatus.PAID);

        // WHEN
        when(repository.getReferenceById(1L)).thenReturn(installment);

        // ASSERT
        var response = useCase.updateStatus(1L, new UpdateInstallmentStatusRequest(InstallmentStatus.OVERDUE));

        assertThat(response.status()).isEqualTo(InstallmentStatus.OVERDUE);
        assertThat(response.paymentDate()).isNull();
    }
}
