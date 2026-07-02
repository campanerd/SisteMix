package org.siste.mix.client.service;

import org.siste.mix.client.dto.CreateClientRequest;
import org.siste.mix.client.dto.UpdateClientRequest;
import org.siste.mix.client.dto.ClientResponse;
import org.siste.mix.client.dto.ClientSummary;
import org.siste.mix.client.model.Client;
import org.siste.mix.client.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository repository;

    @Transactional
    public Client create(CreateClientRequest data) {
        return repository.save(new Client(data));
    }

    public Page<ClientSummary> list(Pageable pageable) {
        return repository.findAllByActiveTrue(pageable).map(ClientSummary::new);
    }

    @Transactional
    public ClientResponse update(UpdateClientRequest data) {
        var client = repository.getReferenceById(data.id());
        client.update(data);
        return new ClientResponse(client);
    }

    @Transactional
    public void delete(Long id) {
        var client = repository.getReferenceById(id);
        client.deactivate();
    }

    public ClientResponse findById(Long id) {
        var client = repository.getReferenceById(id);
        return new ClientResponse(client);
    }
}
