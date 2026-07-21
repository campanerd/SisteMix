package org.siste.mix.client.usecase;

import org.siste.mix.client.dto.CreateClientRequest;
import org.siste.mix.client.model.Client;
import org.siste.mix.client.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateClientUseCase {

    @Autowired
    private ClientRepository repository;

    @Transactional
    public Client create(CreateClientRequest data) {
        return repository.save(new Client(data));
    }
}
