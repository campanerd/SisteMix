package org.siste.mix.user.usecase;

import jakarta.persistence.EntityNotFoundException;
import org.siste.mix.user.dto.UpdateUserRequest;
import org.siste.mix.user.dto.UserResponse;
import org.siste.mix.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateUserUseCase {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse update(UpdateUserRequest data) {
        var user = repository.findById(data.id())
                .filter(u -> Boolean.TRUE.equals(u.getActive()))
                .orElseThrow(EntityNotFoundException::new);
        var hashedPassword = data.password() != null ? passwordEncoder.encode(data.password()) : null;
        user.update(data, hashedPassword);
        return new UserResponse(user);
    }
}
