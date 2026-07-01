package org.example.cliente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repository;

    @Transactional
    public Cliente create(CreateClientRequest data) {
        return repository.save(new Cliente(data));
    }

    public Page<ClientSummary> list(Pageable pageable) {
        return repository.findAllByAtivoTrue(pageable).map(ClientSummary::new);
    }

    @Transactional
    public ClientResponse update(UpdateClientRequest data) {
        var cliente = repository.getReferenceById(data.id());
        cliente.update(data);
        return new ClientResponse(cliente);
    }

    @Transactional
    public void delete(Long id) {
        var cliente = repository.getReferenceById(id);
        cliente.deactivate();
    }

    public ClientResponse findById(Long id) {
        var cliente = repository.getReferenceById(id);
        return new ClientResponse(cliente);
    }
}
