package org.example.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.example.cliente.ClienteRepository;
import org.example.pedido.*;
import org.example.vendedor.VendedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("pedidos")
public class PedidoController {

    @Autowired
    private PedidoRepository repository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VendedorRepository vendedorRepository;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroPedido dados, UriComponentsBuilder uriBuilder) {
        var cliente = clienteRepository.getReferenceById(dados.idCliente());
        var vendedor = vendedorRepository.getReferenceById(dados.idVendedor());
        var pedido = repository.save(new Pedido(dados, cliente, vendedor));
        var uri = uriBuilder.path("/pedidos/{id}").buildAndExpand(pedido.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosDetalhamentoPedido(pedido));
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemPedido>> listar(@PageableDefault(size = 10, sort = {"dataPedido"}) Pageable paginacao) {
        var page = repository.findAllByAtivoTrue(paginacao).map(DadosListagemPedido::new);
        return ResponseEntity.ok(page);
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoPedido dados) {
        var pedido = repository.getReferenceById(dados.id());
        var vendedor = dados.idVendedor() != null
                ? vendedorRepository.getReferenceById(dados.idVendedor())
                : null;
        pedido.atualizarInformacoes(dados, vendedor);
        return ResponseEntity.ok(new DadosDetalhamentoPedido(pedido));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity excluir(@PathVariable Long id) {
        var pedido = repository.getReferenceById(id);
        pedido.excluir();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity detalhar(@PathVariable Long id) {
        var pedido = repository.getReferenceById(id);
        return ResponseEntity.ok(new DadosDetalhamentoPedido(pedido));
    }
}
