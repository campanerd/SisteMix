package org.example.vendedor.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.vendedor.dto.CreateSellerRequest;
import org.example.vendedor.dto.UpdateSellerRequest;

@Table(name = "vendedores")
@Entity(name = "Seller")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cpf;
    private String telefone;
    private Boolean ativo;

    public Seller(CreateSellerRequest data) {
        this.nome = data.nome();
        this.cpf = data.cpf();
        this.telefone = data.telefone();
        this.ativo = true;
    }

    public void update(UpdateSellerRequest data) {
        if (data.nome() != null) this.nome = data.nome();
        if (data.telefone() != null) this.telefone = data.telefone();
    }

    public void deactivate() {
        this.ativo = false;
    }
}