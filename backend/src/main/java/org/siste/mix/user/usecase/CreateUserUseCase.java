package org.siste.mix.user.usecase;

import org.siste.mix.user.dto.CreateUserRequest;
import org.siste.mix.user.dto.UserResponse;
import org.siste.mix.user.model.User;
import org.siste.mix.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateUserUseCase {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse create(CreateUserRequest data) {
        var hashedPassword = passwordEncoder.encode(data.password());
        var user = repository.save(new User(data, hashedPassword));
        return new UserResponse(user);
    }
}
