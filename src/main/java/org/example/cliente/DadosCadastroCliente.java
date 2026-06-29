package org.example.cliente;

import jakarta.validation.constraints.NotBlank;

public record DadosCadastroCliente(
        @NotBlank String nome,
        String telefone,
        String cpfCnpj,
        String email
) {}
