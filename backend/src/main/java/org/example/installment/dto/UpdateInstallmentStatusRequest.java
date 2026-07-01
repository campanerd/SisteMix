package org.example.installment.dto;

import jakarta.validation.constraints.NotNull;
import org.example.installment.enums.InstallmentStatus;

public record UpdateInstallmentStatusRequest(
        @NotNull InstallmentStatus status
) {}
