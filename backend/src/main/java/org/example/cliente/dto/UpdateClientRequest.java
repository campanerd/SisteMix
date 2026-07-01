package org.example.cliente.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateClientRequest(
        @NotNull Long id,
        String nome,
        String telefone,
        String email
) {}
