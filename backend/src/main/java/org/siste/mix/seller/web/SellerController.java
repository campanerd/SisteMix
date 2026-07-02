package org.siste.mix.seller.web;

import jakarta.validation.Valid;
import org.example.seller.dto.*;
import org.siste.mix.seller.dto.CreateSellerRequest;
import org.siste.mix.seller.dto.SellerResponse;
import org.siste.mix.seller.dto.SellerSummary;
import org.siste.mix.seller.dto.UpdateSellerRequest;
import org.siste.mix.seller.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("sellers")
public class SellerController {

    @Autowired
    private SellerService service;

    @PostMapping
    public ResponseEntity<SellerResponse> create(@RequestBody @Valid CreateSellerRequest data, UriComponentsBuilder uriBuilder) {
        var seller = service.create(data);
        var uri = uriBuilder.path("/sellers/{id}").buildAndExpand(seller.getId()).toUri();
        return ResponseEntity.created(uri).body(new SellerResponse(seller));
    }

    @GetMapping
    public ResponseEntity<Page<SellerSummary>> list(@PageableDefault(size = 10, sort = {"name"}) Pageable pageable) {
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
