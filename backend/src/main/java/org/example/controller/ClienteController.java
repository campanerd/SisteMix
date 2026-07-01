package org.example.controller;

import jakarta.validation.Valid;
import org.example.cliente.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("clientes")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @PostMapping
    public ResponseEntity<ClientResponse> create(@RequestBody @Valid CreateClientRequest data, UriComponentsBuilder uriBuilder) {
        var cliente = service.create(data);
        var uri = uriBuilder.path("/clientes/{id}").buildAndExpand(cliente.getId()).toUri();
        return ResponseEntity.created(uri).body(new ClientResponse(cliente));
    }

    @GetMapping
    public ResponseEntity<Page<ClientSummary>> list(@PageableDefault(size = 10, sort = {"nome"}) Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @PutMapping
    public ResponseEntity<ClientResponse> update(@RequestBody @Valid UpdateClientRequest data) {
        return ResponseEntity.ok(service.update(data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
