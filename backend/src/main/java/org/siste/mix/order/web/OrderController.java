package org.siste.mix.order.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.siste.mix.order.dto.CreateOrderRequest;
import org.siste.mix.order.dto.OrderResponse;
import org.siste.mix.order.dto.OrderSummary;
import org.siste.mix.order.dto.UpdateOrderRequest;
import org.siste.mix.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/orders")
@Tag(name = "Pedidos", description = "Cadastro e consulta de pedidos")
public class OrderController {

    @Autowired
    private OrderService service;

    @PostMapping
    @Operation(summary = "Cadastra um novo pedido e gera parcelas automaticamente")
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid CreateOrderRequest data, UriComponentsBuilder uriBuilder) {
        var response = service.create(data);
        var uri = uriBuilder.path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar pedidos")
    public ResponseEntity<Page<OrderSummary>> list(@PageableDefault(size = 10, sort = {"orderDate"}) Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @PutMapping
    @Operation(summary = "Atualizar um pedido")
    public ResponseEntity<OrderResponse> update(@RequestBody @Valid UpdateOrderRequest data) {
        return ResponseEntity.ok(service.update(data));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um pedido")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Listar um pedido por id")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
