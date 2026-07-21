package org.siste.mix.client.usecase;

import org.siste.mix.client.dto.ClientResponse;
import org.siste.mix.client.dto.UpdateClientRequest;
import org.siste.mix.client.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateClientUseCase {

    @Autowired
    private ClientRepository repository;

    @Transactional
    public ClientResponse update(UpdateClientRequest data) {
        var client = repository.getReferenceById(data.id());
        client.update(data);
        return new ClientResponse(client);
    }
}
