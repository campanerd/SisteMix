package org.siste.mix.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.siste.mix.user.enums.UserRole;

public record UpdateUserRequest(
        @NotNull Long id,
        String name,
        @Email String email,
        String password,
        UserRole role
) {}
