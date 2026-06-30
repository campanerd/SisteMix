package org.example.cliente;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoCliente(
        @NotNull Long id,
        String nome,
        String telefone,
        String email
) {}
