package org.siste.mix.seller.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.siste.mix.seller.dto.CreateSellerRequest;
import org.siste.mix.seller.dto.SellerResponse;
import org.siste.mix.seller.dto.SellerSummary;
import org.siste.mix.seller.dto.UpdateSellerRequest;
import org.siste.mix.seller.usecase.CreateSellerUseCase;
import org.siste.mix.seller.usecase.DeleteSellerUseCase;
import org.siste.mix.seller.usecase.FindSellerByIdUseCase;
import org.siste.mix.seller.usecase.ListSellersUseCase;
import org.siste.mix.seller.usecase.UpdateSellerUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("sellers")
@Tag(name = "Vendedores", description = "Cadastro e consulta de vendedores")
public class SellerController {

    @Autowired
    private CreateSellerUseCase createSellerUseCase;

    @Autowired
    private ListSellersUseCase listSellersUseCase;

    @Autowired
    private UpdateSellerUseCase updateSellerUseCase;

    @Autowired
    private DeleteSellerUseCase deleteSellerUseCase;

    @Autowired
    private FindSellerByIdUseCase findSellerByIdUseCase;

    @PostMapping
    @Operation(summary = "Cadastra um novo vendedor")
    public ResponseEntity<SellerResponse> create(@RequestBody @Valid CreateSellerRequest data, UriComponentsBuilder uriBuilder) {
        var seller = createSellerUseCase.create(data);
        var uri = uriBuilder.path("/{id}").buildAndExpand(seller.getId()).toUri();
        return ResponseEntity.created(uri).body(new SellerResponse(seller));
    }

    @GetMapping
    @Operation(summary = "Listar vendedores")
    public ResponseEntity<Page<SellerSummary>> list(@PageableDefault(size = 10, sort = {"name"}) Pageable pageable) {
        return ResponseEntity.ok(listSellersUseCase.list(pageable));
    }

    @PutMapping
    @Operation(summary = "Atualizar um vendedor")
    public ResponseEntity<SellerResponse> update(@RequestBody @Valid UpdateSellerRequest data) {
        return ResponseEntity.ok(updateSellerUseCase.update(data));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um vendedor")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteSellerUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Listar um vendedor por id")
    public ResponseEntity<SellerResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(findSellerByIdUseCase.findById(id));
    }
}
