package org.example.vendedor;

import jakarta.validation.constraints.NotBlank;

public record DadosCadastroVendedor(
        @NotBlank String nome,
        String cpf,
        String telefone
) {}
