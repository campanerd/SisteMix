package org.siste.mix.user.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.siste.mix.user.dto.CreateUserRequest;
import org.siste.mix.user.dto.UpdateUserRequest;
import org.siste.mix.user.dto.UserResponse;
import org.siste.mix.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuários", description = "Cadastro e consulta de Usuários")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody @Valid CreateUserRequest data, UriComponentsBuilder uriBuilder) {
        var user = service.create(data);
        var uri = uriBuilder.path("/api/users/{id}").buildAndExpand(user.id()).toUri();
        return ResponseEntity.created(uri).body(user);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> list() {
        return ResponseEntity.ok(service.list());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    @Operation(summary = "Atualizar um usuário")
    public ResponseEntity<UserResponse> update(@RequestBody @Valid UpdateUserRequest data) {
        return ResponseEntity.ok(service.update(data));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Listar um usuário por id")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
