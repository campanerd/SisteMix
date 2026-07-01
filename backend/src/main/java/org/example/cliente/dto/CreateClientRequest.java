package org.example.cliente.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateClientRequest(
        @NotBlank String nome,
        String telefone,
        String cpfCnpj,
        String email
) {}
