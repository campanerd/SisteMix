package org.example.vendedor;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "vendedores")
@Entity(name = "Vendedor")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Vendedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cpf;
    private String telefone;
    private Boolean ativo;

    public Vendedor(CreateSellerRequest data) {
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
