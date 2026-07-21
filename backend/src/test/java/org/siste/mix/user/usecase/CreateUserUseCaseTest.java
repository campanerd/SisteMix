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
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CreateUserUseCase useCase;

    @Test
    void should_hash_password_when_creating_user() {
        var request = new CreateUserRequest("João Silva", "joao@email.com", "123456", UserRole.ROLE_USER);

        // WHEN
        when(passwordEncoder.encode("123456")).thenReturn("hashed-password");
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ASSERT
        var response = useCase.create(request);

        assertThat(response.name()).isEqualTo("João Silva");
        assertThat(response.email()).isEqualTo("joao@email.com");
        verify(passwordEncoder).encode("123456");
    }
}
