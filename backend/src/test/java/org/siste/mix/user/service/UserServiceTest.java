package org.siste.mix.user.service;

import jakarta.persistence.EntityNotFoundException;
import org.siste.mix.user.dto.CreateUserRequest;
import org.siste.mix.user.dto.UpdateUserRequest;
import org.siste.mix.user.enums.UserRole;
import org.siste.mix.user.model.User;
import org.siste.mix.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService service;

    @Test
    void should_hash_password_when_creating_user() {
        var request = new CreateUserRequest("João Silva", "joao@email.com", "123456", UserRole.ROLE_USER);

        // WHEN
        when(passwordEncoder.encode("123456")).thenReturn("hashed-password");
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ASSERT
        var response = service.create(request);

        assertThat(response.name()).isEqualTo("João Silva");
        assertThat(response.email()).isEqualTo("joao@email.com");
        verify(passwordEncoder).encode("123456");
    }

    @Test
    void should_list_only_active_users() {
        var active = new User(new CreateUserRequest("Ativo", "ativo@email.com", "123456", UserRole.ROLE_USER), "hash");
        var inactive = new User(new CreateUserRequest("Inativo", "inativo@email.com", "123456", UserRole.ROLE_USER), "hash");
        inactive.deactivate();

        // WHEN
        when(repository.findAll()).thenReturn(List.of(active, inactive));

        // ASSERT
        var result = service.list();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Ativo");
    }

    @Test
    void should_throw_when_deactivating_nonexistent_user() {
        // WHEN
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // ASSERT
        assertThatThrownBy(() -> service.deactivate(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void should_throw_when_deactivating_already_inactive_user() {
        var user = new User(new CreateUserRequest("João Silva", "joao@email.com", "123456", UserRole.ROLE_USER), "hash");
        user.deactivate();

        // WHEN
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        // ASSERT
        assertThatThrownBy(() -> service.deactivate(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void should_rehash_password_only_when_provided_in_update() {
        var user = new User(new CreateUserRequest("João Silva", "joao@email.com", "123456", UserRole.ROLE_USER), "old-hash");

        // WHEN
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpass")).thenReturn("new-hash");

        // ASSERT
        service.update(new UpdateUserRequest(1L, null, null, "newpass", null));

        assertThat(user.getPassword()).isEqualTo("new-hash");
    }

    @Test
    void should_not_rehash_password_when_not_provided_in_update() {
        var user = new User(new CreateUserRequest("João Silva", "joao@email.com", "123456", UserRole.ROLE_USER), "old-hash");

        // WHEN
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        // ASSERT
        service.update(new UpdateUserRequest(1L, "Novo Nome", null, null, null));

        assertThat(user.getPassword()).isEqualTo("old-hash");
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void should_throw_when_updating_nonexistent_user() {
        // WHEN
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // ASSERT
        assertThatThrownBy(() -> service.update(new UpdateUserRequest(1L, "Nome", null, null, null)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void should_throw_when_finding_inactive_user_by_id() {
        var user = new User(new CreateUserRequest("João Silva", "joao@email.com", "123456", UserRole.ROLE_USER), "hash");
        user.deactivate();

        // WHEN
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        // ASSERT
        assertThatThrownBy(() -> service.findById(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
