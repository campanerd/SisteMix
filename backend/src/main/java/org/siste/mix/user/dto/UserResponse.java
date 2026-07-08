package org.siste.mix.user.dto;

import org.siste.mix.user.enums.UserRole;
import org.siste.mix.user.model.User;

public record UserResponse(
        Long id,
        String name,
        String email,
        UserRole role
) {
    public UserResponse(User user) {
        this(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
