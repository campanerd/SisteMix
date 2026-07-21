package org.siste.mix.user.usecase;

import jakarta.persistence.EntityNotFoundException;
import org.siste.mix.user.dto.CreateUserRequest;
import org.siste.mix.user.enums.UserRole;
import org.siste.mix.user.model.User;
import org.siste.mix.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeactivateUserUseCaseTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private DeactivateUserUseCase useCase;

    @Test
    void should_throw_when_deactivating_nonexistent_user() {
        // WHEN
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // ASSERT
        assertThatThrownBy(() -> useCase.deactivate(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void should_throw_when_deactivating_already_inactive_user() {
        var user = new User(new CreateUserRequest("João Silva", "joao@email.com", "123456", UserRole.ROLE_USER), "hash");
        user.deactivate();

        // WHEN
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        // ASSERT
        assertThatThrownBy(() -> useCase.deactivate(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
