package org.siste.mix.client.usecase;

import org.siste.mix.client.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteClientUseCase {

    @Autowired
    private ClientRepository repository;

    @Transactional
    public void delete(Long id) {
        var client = repository.getReferenceById(id);
        client.deactivate();
    }
}
