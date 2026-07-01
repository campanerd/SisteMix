package org.example.cliente;

import jakarta.validation.constraints.NotNull;

public record UpdateClientRequest(
        @NotNull Long id,
        String nome,
        String telefone,
        String email
) {}
