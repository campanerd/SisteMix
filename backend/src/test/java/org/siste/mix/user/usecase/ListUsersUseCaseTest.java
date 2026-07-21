package org.siste.mix.user.usecase;

import org.siste.mix.user.dto.CreateUserRequest;
import org.siste.mix.user.enums.UserRole;
import org.siste.mix.user.model.User;
import org.siste.mix.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListUsersUseCaseTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private ListUsersUseCase useCase;

    @Test
    void should_list_only_active_users() {
        var active = new User(new CreateUserRequest("Ativo", "ativo@email.com", "123456", UserRole.ROLE_USER), "hash");
        var inactive = new User(new CreateUserRequest("Inativo", "inativo@email.com", "123456", UserRole.ROLE_USER), "hash");
        inactive.deactivate();

        // WHEN
        when(repository.findAll()).thenReturn(List.of(active, inactive));

        // ASSERT
        var result = useCase.list();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Ativo");
    }
}
