package org.siste.mix.order.usecase;

import org.siste.mix.installment.model.Installment;
import org.siste.mix.installment.repository.InstallmentRepository;
import org.siste.mix.order.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class InstallmentGenerator {

    @Autowired
    private InstallmentRepository installmentRepository;

    public void generate(Order order) {
        int total = order.getTotalInstallments();
        BigDecimal installmentAmount = order.getTotalAmount()
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.DOWN);
        BigDecimal lastAmount = order.getTotalAmount()
                .subtract(installmentAmount.multiply(BigDecimal.valueOf(total - 1)));

        for (int i = 1; i <= total; i++) {
            BigDecimal amount = (i == total) ? lastAmount : installmentAmount;
            var dueDate = order.getOrderDate().plusMonths(i);
            installmentRepository.save(new Installment(i, amount, dueDate, order));
        }
    }
}
