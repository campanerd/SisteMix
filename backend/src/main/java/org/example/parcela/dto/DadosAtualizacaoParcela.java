package org.example.parcela.dto;

import jakarta.validation.constraints.NotNull;
import org.example.parcela.enums.StatusParcela;

public record DadosAtualizacaoParcela(
        @NotNull StatusParcela status
) {}
