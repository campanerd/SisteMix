package org.siste.mix.user.enums;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ROLE_USER,
    ROLE_ADMIN,
    ROLE_DEV;

    @Override
    public String getAuthority() {
        return name();
    }
}
