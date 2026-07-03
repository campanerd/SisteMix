package org.siste.mix.installment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.siste.mix.installment.enums.InstallmentStatus;
import org.siste.mix.order.model.Order;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "installments")
@Entity(name = "Installment")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Installment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "installment_number")
    private Integer installmentNumber;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private InstallmentStatus status;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public Installment(Integer installmentNumber, BigDecimal amount, LocalDate dueDate, Order order) {
        this.installmentNumber = installmentNumber;
        this.amount = amount;
        this.dueDate = dueDate;
        this.status = InstallmentStatus.PENDING;
        this.order = order;
    }

    public void updateStatus(InstallmentStatus newStatus) {
        this.status = newStatus;
        this.paymentDate = newStatus == InstallmentStatus.PAID ? LocalDate.now() : null;
    }
}
