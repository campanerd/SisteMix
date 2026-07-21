package org.siste.mix.client.usecase;

import org.siste.mix.client.dto.ClientSummary;
import org.siste.mix.client.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListClientsUseCase {

    @Autowired
    private ClientRepository repository;

    public Page<ClientSummary> list(Pageable pageable) {
        return repository.findAllByActiveTrue(pageable).map(ClientSummary::new);
    }
}
