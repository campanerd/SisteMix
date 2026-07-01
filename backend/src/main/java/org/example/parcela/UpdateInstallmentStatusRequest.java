package org.example.parcela;

import jakarta.validation.constraints.NotNull;

public record UpdateInstallmentStatusRequest(
        @NotNull InstallmentStatus status
) {}
