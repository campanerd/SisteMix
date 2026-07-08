package org.siste.mix.user.service;

import jakarta.persistence.EntityNotFoundException;
import org.siste.mix.user.dto.CreateUserRequest;
import org.siste.mix.user.dto.UserResponse;
import org.siste.mix.user.model.User;
import org.siste.mix.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

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

    public List<UserResponse> list() {
        return repository.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getActive()))
                .map(UserResponse::new)
                .toList();
    }

    @Transactional
    public void deactivate(Long id) {
        var user = repository.findById(id)
                .filter(u -> Boolean.TRUE.equals(u.getActive()))
                .orElseThrow(EntityNotFoundException::new);
        user.deactivate();
    }
}
