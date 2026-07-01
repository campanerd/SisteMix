package org.example.seller.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSellerRequest(
        @NotBlank String name,
        String cpf,
        String phone
) {}
