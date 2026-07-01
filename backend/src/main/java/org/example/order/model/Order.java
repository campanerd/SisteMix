package org.example.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.client.model.Client;
import org.example.order.dto.CreateOrderRequest;
import org.example.order.dto.UpdateOrderRequest;
import org.example.seller.model.Seller;

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

    @Column(name = "active")
    private Boolean active;

    public Order(CreateOrderRequest data, Client client, Seller seller) {
        this.orderNumber = data.orderNumber();
        this.issueDate = data.issueDate();
        this.orderDate = data.orderDate();
        this.totalAmount = data.totalAmount();
        this.totalInstallments = data.totalInstallments();
        this.notes = data.notes();
        this.client = client;
        this.seller = seller;
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
