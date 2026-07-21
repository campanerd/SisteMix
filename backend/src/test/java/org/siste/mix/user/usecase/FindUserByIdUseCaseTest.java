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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindUserByIdUseCaseTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private FindUserByIdUseCase useCase;

    @Test
    void should_return_user_when_active() {
        var user = new User(new CreateUserRequest("João Silva", "joao@email.com", "123456", UserRole.ROLE_USER), "hash");

        // WHEN
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        // ASSERT
        var result = useCase.findById(1L);

        assertThat(result.name()).isEqualTo("João Silva");
    }

    @Test
    void should_throw_when_finding_inactive_user_by_id() {
        var user = new User(new CreateUserRequest("João Silva", "joao@email.com", "123456", UserRole.ROLE_USER), "hash");
        user.deactivate();

        // WHEN
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        // ASSERT
        assertThatThrownBy(() -> useCase.findById(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
