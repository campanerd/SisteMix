package org.siste.mix.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.siste.mix.client.model.Client;
import org.siste.mix.order.dto.CreateOrderRequest;
import org.siste.mix.order.dto.UpdateOrderRequest;
import org.siste.mix.seller.model.Seller;
import org.siste.mix.user.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "orders")
@Entity(name = "Order")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "total_installments")
    private Integer totalInstallments;

    @Column(name = "notes")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "active")
    private Boolean active;

    public Order(CreateOrderRequest data, Client client, Seller seller, User createdBy) {
        this.orderNumber = data.orderNumber();
        this.issueDate = data.issueDate();
        this.orderDate = data.orderDate();
        this.totalAmount = data.totalAmount();
        this.totalInstallments = data.totalInstallments();
        this.notes = data.notes();
        this.client = client;
        this.seller = seller;
        this.createdBy = createdBy;
        this.active = true;
    }

    public void update(UpdateOrderRequest data, Seller seller) {
        if (data.issueDate() != null) this.issueDate = data.issueDate();
        if (data.orderDate() != null) this.orderDate = data.orderDate();
        if (data.totalAmount() != null) this.totalAmount = data.totalAmount();
        if (data.notes() != null) this.notes = data.notes();
        if (seller != null) this.seller = seller;
    }

    public void deactivate() {
        this.active = false;
    }
}
