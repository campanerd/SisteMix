package org.siste.mix.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.siste.mix.order.enums.OrderHistoryAction;
import org.siste.mix.user.model.User;

import java.time.LocalDateTime;

@Table(name = "order_history")
@Entity(name = "OrderHistory")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class OrderHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private OrderHistoryAction action;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "changes")
    private String changes;

    public OrderHistory(Order order, User user, OrderHistoryAction action, String changes) {
        this.order = order;
        this.user = user;
        this.action = action;
        this.changes = changes;
        this.changedAt = LocalDateTime.now();
    }
}
