package org.siste.mix.user.model;

import org.siste.mix.user.dto.CreateUserRequest;
import org.siste.mix.user.dto.UpdateUserRequest;
import org.siste.mix.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void should_create_user_with_hashed_password_and_active_true() {
        var request = new CreateUserRequest("João Silva", "joao@email.com", "123456", UserRole.ROLE_USER);
        var user = new User(request, "hashed-password");

        assertThat(user.getName()).isEqualTo("João Silva");
        assertThat(user.getEmail()).isEqualTo("joao@email.com");
        assertThat(user.getPassword()).isEqualTo("hashed-password");
        assertThat(user.getRole()).isEqualTo(UserRole.ROLE_USER);
        assertThat(user.getActive()).isTrue();
    }

    @Test
    void should_update_only_provided_fields() {
        var user = new User(new CreateUserRequest("João Silva", "joao@email.com", "123456", UserRole.ROLE_USER), "hashed-password");

        user.update(new UpdateUserRequest(null, "José Santos", null, null, null), null);

        assertThat(user.getName()).isEqualTo("José Santos");
        assertThat(user.getEmail()).isEqualTo("joao@email.com");
        assertThat(user.getRole()).isEqualTo(UserRole.ROLE_USER);
    }

    @Test
    void should_update_password_only_when_hashed_password_is_provided() {
        var user = new User(new CreateUserRequest("João Silva", "joao@email.com", "123456", UserRole.ROLE_USER), "old-hash");

        user.update(new UpdateUserRequest(null, null, null, null, null), "new-hash");

        assertThat(user.getPassword()).isEqualTo("new-hash");
    }

    @Test
    void should_keep_password_when_hashed_password_is_null() {
        var user = new User(new CreateUserRequest("João Silva", "joao@email.com", "123456", UserRole.ROLE_USER), "old-hash");

        user.update(new UpdateUserRequest(null, null, null, null, null), null);

        assertThat(user.getPassword()).isEqualTo("old-hash");
    }

    @Test
    void should_keep_all_fields_when_update_is_null() {
        var user = new User(new CreateUserRequest("João Silva", "joao@email.com", "123456", UserRole.ROLE_USER), "hashed-password");

        user.update(new UpdateUserRequest(null, null, null, null, null), null);

        assertThat(user.getName()).isEqualTo("João Silva");
        assertThat(user.getEmail()).isEqualTo("joao@email.com");
        assertThat(user.getRole()).isEqualTo(UserRole.ROLE_USER);
    }

    @Test
    void should_set_active_to_false_when_deactivated() {
        var user = new User(new CreateUserRequest("João Silva", "joao@email.com", "123456", UserRole.ROLE_USER), "hashed-password");

        assertThat(user.getActive()).isTrue();
        user.deactivate();
        assertThat(user.getActive()).isFalse();
    }

    @Test
    void should_return_role_as_single_authority() {
        var user = new User(new CreateUserRequest("João Silva", "joao@email.com", "123456", UserRole.ROLE_ADMIN), "hashed-password");

        assertThat(user.getAuthorities()).extracting(GrantedAuthority::getAuthority).containsExactly("ROLE_ADMIN");
    }

    @Test
    void should_return_email_as_username() {
        var user = new User(new CreateUserRequest("João Silva", "joao@email.com", "123456", UserRole.ROLE_USER), "hashed-password");

        assertThat(user.getUsername()).isEqualTo("joao@email.com");
    }

    @Test
    void should_be_enabled_only_when_active() {
        var user = new User(new CreateUserRequest("João Silva", "joao@email.com", "123456", UserRole.ROLE_USER), "hashed-password");

        assertThat(user.isEnabled()).isTrue();
        user.deactivate();
        assertThat(user.isEnabled()).isFalse();
    }
}
