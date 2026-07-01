package org.example.cliente.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.cliente.dto.CreateClientRequest;
import org.example.cliente.dto.UpdateClientRequest;

@Table(name = "clientes")
@Entity(name = "Client")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String telefone;
    private String cpfCnpj;
    private String email;
    private Boolean ativo;

    public Client(CreateClientRequest data) {
        this.nome = data.nome();
        this.telefone = data.telefone();
        this.cpfCnpj = data.cpfCnpj();
        this.email = data.email();
        this.ativo = true;
    }

    public void update(UpdateClientRequest data) {
        if (data.nome() != null) this.nome = data.nome();
        if (data.telefone() != null) this.telefone = data.telefone();
        if (data.email() != null) this.email = data.email();
    }

    public void deactivate() {
        this.ativo = false;
    }
}
