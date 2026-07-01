package org.example.vendedor;

import jakarta.validation.constraints.NotNull;

public record UpdateSellerRequest(
        @NotNull Long id,
        String nome,
        String telefone
) {}
