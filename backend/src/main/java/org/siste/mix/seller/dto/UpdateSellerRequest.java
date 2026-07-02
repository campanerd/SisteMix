package org.siste.mix.seller.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateSellerRequest(
        @NotNull Long id,
        String name,
        String phone
) {}
