package org.siste.mix.client.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.siste.mix.client.dto.CreateClientRequest;
import org.siste.mix.client.dto.UpdateClientRequest;
import org.siste.mix.client.dto.ClientResponse;
import org.siste.mix.client.dto.ClientSummary;
import org.siste.mix.client.usecase.CreateClientUseCase;
import org.siste.mix.client.usecase.DeleteClientUseCase;
import org.siste.mix.client.usecase.FindClientByIdUseCase;
import org.siste.mix.client.usecase.ListClientsUseCase;
import org.siste.mix.client.usecase.UpdateClientUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/clients")
@Tag(name = "Clientes", description = "Cadastro e consulta de clientes")
public class ClientController {

    @Autowired
    private CreateClientUseCase createClientUseCase;

    @Autowired
    private ListClientsUseCase listClientsUseCase;

    @Autowired
    private UpdateClientUseCase updateClientUseCase;

    @Autowired
    private DeleteClientUseCase deleteClientUseCase;

    @Autowired
    private FindClientByIdUseCase findClientByIdUseCase;

    @PostMapping
    @Operation(summary = "Cadastra um novo cliente")
    public ResponseEntity<ClientResponse> create(@RequestBody @Valid CreateClientRequest data, UriComponentsBuilder uriBuilder) {
        var client = createClientUseCase.create(data);
        var uri = uriBuilder.path("/{id}").buildAndExpand(client.getId()).toUri();
        return ResponseEntity.created(uri).body(new ClientResponse(client));
    }

    @GetMapping
    @Operation(summary = "Listar clientes")
    public ResponseEntity<Page<ClientSummary>> list(@PageableDefault(size = 10, sort = {"name"}) Pageable pageable) {
        return ResponseEntity.ok(listClientsUseCase.list(pageable));
    }

    @PutMapping
    @Operation(summary = "Atualizar um cliente")
    public ResponseEntity<ClientResponse> update(@RequestBody @Valid UpdateClientRequest data) {
        return ResponseEntity.ok(updateClientUseCase.update(data));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um cliente")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteClientUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Listar um cliente por id")
    public ResponseEntity<ClientResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(findClientByIdUseCase.findById(id));
    }
}
