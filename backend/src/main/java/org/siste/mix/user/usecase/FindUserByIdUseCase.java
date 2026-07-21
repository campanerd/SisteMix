package org.siste.mix.user.usecase;

import jakarta.persistence.EntityNotFoundException;
import org.siste.mix.user.dto.UserResponse;
import org.siste.mix.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindUserByIdUseCase {

    @Autowired
    private UserRepository repository;

    public UserResponse findById(Long id) {
        var user = repository.findById(id)
                .filter(u -> Boolean.TRUE.equals(u.getActive()))
                .orElseThrow(EntityNotFoundException::new);
        return new UserResponse(user);
    }
}
