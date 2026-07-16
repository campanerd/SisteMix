package org.siste.mix.user.dto;

import org.siste.mix.user.model.User;

public record UserResponse(
        Long id,
        String name,
        String email
) {
    public UserResponse(User user) {
        this(user.getId(), user.getName(), user.getEmail());
    }
}
