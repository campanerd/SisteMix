package org.example.pedido.web;

import jakarta.validation.Valid;
import org.example.pedido.dto.CreateOrderRequest;
import org.example.pedido.dto.OrderResponse;
import org.example.pedido.dto.OrderSummary;
import org.example.pedido.dto.UpdateOrderRequest;
import org.example.pedido.service.PedidoService;
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
    private PedidoService service;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid CreateOrderRequest data, UriComponentsBuilder uriBuilder) {
        var response = service.create(data);
        var uri = uriBuilder.path("/pedidos/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<OrderSummary>> list(@PageableDefault(size = 10, sort = {"dataPedido"}) Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @PutMapping
    public ResponseEntity<OrderResponse> update(@RequestBody @Valid UpdateOrderRequest data) {
        return ResponseEntity.ok(service.update(data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
