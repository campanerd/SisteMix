package org.siste.mix.installment.repository;

import org.siste.mix.installment.enums.InstallmentStatus;
import org.siste.mix.installment.model.Installment;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InstallmentSpec {

    public static Specification<Installment> orderIsActive() {
        return (root, query, cb) -> cb.isTrue(root.get("order").get("active"));
    }

    public static Specification<Installment> forOrder(Long orderId) {
        return (root, query, cb) -> cb.equal(root.get("order").get("id"), orderId);
    }

    public static Specification<Installment> hasStatus(InstallmentStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Installment> dueDateFrom(LocalDate from) {
        return (root, query, cb) -> from == null ? null : cb.greaterThanOrEqualTo(root.get("dueDate"), from);
    }

    public static Specification<Installment> dueDateTo(LocalDate to) {
        return (root, query, cb) -> to == null ? null : cb.lessThanOrEqualTo(root.get("dueDate"), to);
    }

    public static Specification<Installment> amountMin(BigDecimal min) {
        return (root, query, cb) -> min == null ? null : cb.greaterThanOrEqualTo(root.get("amount"), min);
    }

    public static Specification<Installment> amountMax(BigDecimal max) {
        return (root, query, cb) -> max == null ? null : cb.lessThanOrEqualTo(root.get("amount"), max);
    }
}
