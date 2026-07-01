package org.example.vendedor.web;

import jakarta.validation.Valid;
import org.example.vendedor.dto.*;
import org.example.vendedor.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("vendedores")
public class SellerController {

    @Autowired
    private SellerService service;

    @PostMapping
    public ResponseEntity<SellerResponse> create(@RequestBody @Valid CreateSellerRequest data, UriComponentsBuilder uriBuilder) {
        var seller = service.create(data);
        var uri = uriBuilder.path("/vendedores/{id}").buildAndExpand(seller.getId()).toUri();
        return ResponseEntity.created(uri).body(new SellerResponse(seller));
    }

    @GetMapping
    public ResponseEntity<Page<SellerSummary>> list(@PageableDefault(size = 10, sort = {"nome"}) Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @PutMapping
    public ResponseEntity<SellerResponse> update(@RequestBody @Valid UpdateSellerRequest data) {
        return ResponseEntity.ok(service.update(data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SellerResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}