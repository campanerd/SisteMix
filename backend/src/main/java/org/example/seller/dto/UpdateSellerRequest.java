package org.example.seller.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateSellerRequest(
        @NotNull Long id,
        String name,
        String phone
) {}
