package org.siste.mix.user.usecase;

import jakarta.persistence.EntityNotFoundException;
import org.siste.mix.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeactivateUserUseCase {

    @Autowired
    private UserRepository repository;

    @Transactional
    public void deactivate(Long id) {
        var user = repository.findById(id)
                .filter(u -> Boolean.TRUE.equals(u.getActive()))
                .orElseThrow(EntityNotFoundException::new);
        user.deactivate();
    }
}
