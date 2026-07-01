package org.example.client.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.client.dto.CreateClientRequest;
import org.example.client.dto.UpdateClientRequest;

@Table(name = "clients")
@Entity(name = "Client")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    private String cpfCnpj;
    private String email;

    @Column(name = "active")
    private Boolean active;

    public Client(CreateClientRequest data) {
        this.name = data.name();
        this.phone = data.phone();
        this.cpfCnpj = data.cpfCnpj();
        this.email = data.email();
        this.active = true;
    }

    public void update(UpdateClientRequest data) {
        if (data.name() != null) this.name = data.name();
        if (data.phone() != null) this.phone = data.phone();
        if (data.email() != null) this.email = data.email();
    }

    public void deactivate() {
        this.active = false;
    }
}
