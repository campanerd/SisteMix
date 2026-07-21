package org.siste.mix.client.usecase;

import org.siste.mix.client.dto.ClientResponse;
import org.siste.mix.client.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindClientByIdUseCase {

    @Autowired
    private ClientRepository repository;

    public ClientResponse findById(Long id) {
        var client = repository.getReferenceById(id);
        return new ClientResponse(client);
    }
}
