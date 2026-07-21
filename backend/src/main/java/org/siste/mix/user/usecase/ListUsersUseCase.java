package org.siste.mix.user.usecase;

import org.siste.mix.user.dto.UserResponse;
import org.siste.mix.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListUsersUseCase {

    @Autowired
    private UserRepository repository;

    public List<UserResponse> list() {
        return repository.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getActive()))
                .map(UserResponse::new)
                .toList();
    }
}
