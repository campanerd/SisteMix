package org.example.vendedor;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoVendedor(
        @NotNull Long id,
        String nome,
        String telefone
) {}
