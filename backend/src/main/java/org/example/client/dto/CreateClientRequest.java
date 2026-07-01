package org.example.client.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateClientRequest(
        @NotBlank String name,
        String phone,
        String cpfCnpj,
        String email
) {}
