package org.siste.mix.installment.dto;

import jakarta.validation.constraints.NotNull;
import org.siste.mix.installment.enums.InstallmentStatus;

public record UpdateInstallmentStatusRequest(
        @NotNull InstallmentStatus status
) {}
