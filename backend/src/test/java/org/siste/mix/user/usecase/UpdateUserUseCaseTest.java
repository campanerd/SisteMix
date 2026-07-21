package org.siste.mix.user.usecase;

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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateUserUseCaseTest {

    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UpdateUserUseCase useCase;

    @Test
    void should_rehash_password_only_when_provided_in_update() {
        var user = new User(new CreateUserRequest("João Silva", "joao@email.com", "123456", UserRole.ROLE_USER), "old-hash");

        // WHEN
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpass")).thenReturn("new-hash");

        // ASSERT
        useCase.update(new UpdateUserRequest(1L, null, null, "newpass", null));

        assertThat(user.getPassword()).isEqualTo("new-hash");
    }

    @Test
    void should_not_rehash_password_when_not_provided_in_update() {
        var user = new User(new CreateUserRequest("João Silva", "joao@email.com", "123456", UserRole.ROLE_USER), "old-hash");

        // WHEN
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        // ASSERT
        useCase.update(new UpdateUserRequest(1L, "Novo Nome", null, null, null));

        assertThat(user.getPassword()).isEqualTo("old-hash");
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void should_throw_when_updating_nonexistent_user() {
        // WHEN
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // ASSERT
        assertThatThrownBy(() -> useCase.update(new UpdateUserRequest(1L, "Nome", null, null, null)))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
