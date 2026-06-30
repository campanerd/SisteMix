package org.example.parcela;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoParcela(
        @NotNull StatusParcela status
) {}
