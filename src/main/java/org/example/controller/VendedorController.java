package org.example.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.example.vendedor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("vendedores")
public class VendedorController {

    @Autowired
    private VendedorRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroVendedor dados, UriComponentsBuilder uriBuilder) {
        var vendedor = repository.save(new Vendedor(dados));
        var uri = uriBuilder.path("/vendedores/{id}").buildAndExpand(vendedor.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosDetalhamentoVendedor(vendedor));
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemVendedor>> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        var page = repository.findAllByAtivoTrue(paginacao).map(DadosListagemVendedor::new);
        return ResponseEntity.ok(page);
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoVendedor dados) {
        var vendedor = repository.getReferenceById(dados.id());
        vendedor.atualizarInformacoes(dados);
        return ResponseEntity.ok(new DadosDetalhamentoVendedor(vendedor));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity excluir(@PathVariable Long id) {
        var vendedor = repository.getReferenceById(id);
        vendedor.excluir();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity detalhar(@PathVariable Long id) {
        var vendedor = repository.getReferenceById(id);
        return ResponseEntity.ok(new DadosDetalhamentoVendedor(vendedor));
    }
}
