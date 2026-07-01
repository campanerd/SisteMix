package org.example.vendedor.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSellerRequest(
        @NotBlank String nome,
        String cpf,
        String telefone
) {}