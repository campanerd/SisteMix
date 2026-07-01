package org.example.client.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateClientRequest(
        @NotNull Long id,
        String name,
        String phone,
        String email
) {}
