package org.example.seller.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.seller.dto.CreateSellerRequest;
import org.example.seller.dto.UpdateSellerRequest;

@Table(name = "sellers")
@Entity(name = "Seller")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    private String cpf;

    @Column(name = "phone")
    private String phone;

    @Column(name = "active")
    private Boolean active;

    public Seller(CreateSellerRequest data) {
        this.name = data.name();
        this.cpf = data.cpf();
        this.phone = data.phone();
        this.active = true;
    }

    public void update(UpdateSellerRequest data) {
        if (data.name() != null) this.name = data.name();
        if (data.phone() != null) this.phone = data.phone();
    }

    public void deactivate() {
        this.active = false;
    }
}
