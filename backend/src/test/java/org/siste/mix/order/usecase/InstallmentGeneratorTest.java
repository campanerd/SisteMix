package org.siste.mix.order.usecase;

import org.siste.mix.installment.model.Installment;
import org.siste.mix.installment.repository.InstallmentRepository;
import org.siste.mix.order.model.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InstallmentGeneratorTest {

    @Mock
    private InstallmentRepository installmentRepository;

    @InjectMocks
    private InstallmentGenerator generator;

    @Test
    void should_generate_correct_number_of_installments() {
        var order = orderWith(new BigDecimal("300.00"), 3, LocalDate.of(2026, 1, 15));

        // ASSERT
        generator.generate(order);

        verify(installmentRepository, times(3)).save(any(Installment.class));
    }

    @Test
    void should_distribute_amount_with_rounding_on_last_installment() {
        var order = orderWith(new BigDecimal("100.00"), 3, LocalDate.of(2026, 1, 15));

        // ASSERT
        generator.generate(order);

        var captor = ArgumentCaptor.forClass(Installment.class);
        verify(installmentRepository, times(3)).save(captor.capture());

        List<Installment> installments = captor.getAllValues();
        assertEquals(0, installments.get(0).getAmount().compareTo(new BigDecimal("33.33")));
        assertEquals(0, installments.get(1).getAmount().compareTo(new BigDecimal("33.33")));
        assertEquals(0, installments.get(2).getAmount().compareTo(new BigDecimal("33.34")));
    }

    @Test
    void should_distribute_equal_amounts_when_division_is_exact() {
        var order = orderWith(new BigDecimal("300.00"), 3, LocalDate.of(2026, 1, 15));

        // ASSERT
        generator.generate(order);

        var captor = ArgumentCaptor.forClass(Installment.class);
        verify(installmentRepository, times(3)).save(captor.capture());

        captor.getAllValues().forEach(i ->
                assertEquals(0, i.getAmount().compareTo(new BigDecimal("100.00")))
        );
    }

    @Test
    void should_set_monthly_due_dates_from_order_date() {
        var orderDate = LocalDate.of(2026, 1, 15);
        var order = orderWith(new BigDecimal("200.00"), 2, orderDate);

        // ASSERT
        generator.generate(order);

        var captor = ArgumentCaptor.forClass(Installment.class);
        verify(installmentRepository, times(2)).save(captor.capture());

        List<Installment> installments = captor.getAllValues();
        assertEquals(LocalDate.of(2026, 2, 15), installments.get(0).getDueDate());
        assertEquals(LocalDate.of(2026, 3, 15), installments.get(1).getDueDate());
    }

    private Order orderWith(BigDecimal totalAmount, int totalInstallments, LocalDate orderDate) {
        return new Order(1L, "PED-001", orderDate, orderDate, totalAmount, totalInstallments, null, null, null, null, true);
    }
}
