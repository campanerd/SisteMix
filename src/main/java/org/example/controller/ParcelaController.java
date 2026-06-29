package org.example.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.example.parcela.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("parcelas")
public class ParcelaController {

    @Autowired
    private ParcelaRepository repository;

    @GetMapping
    public ResponseEntity<List<DadosListagemParcela>> listar(
            @RequestParam(required = false) StatusParcela status,
            @RequestParam(required = false) LocalDate vencimentoInicio,
            @RequestParam(required = false) LocalDate vencimentoFim,
            @RequestParam(required = false) BigDecimal valorMin,
            @RequestParam(required = false) BigDecimal valorMax
    ) {
        var parcelas = repository.findWithFilters(status, vencimentoInicio, vencimentoFim, valorMin, valorMax)
                .stream().map(DadosListagemParcela::new).toList();
        return ResponseEntity.ok(parcelas);
    }

    @GetMapping("/{id}")
    public ResponseEntity detalhar(@PathVariable Long id) {
        var parcela = repository.getReferenceById(id);
        return ResponseEntity.ok(new DadosDetalhamentoParcela(parcela));
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<List<DadosListagemParcela>> listarPorPedido(@PathVariable Long idPedido) {
        var parcelas = repository.findByPedidoId(idPedido)
                .stream().map(DadosListagemParcela::new).toList();
        return ResponseEntity.ok(parcelas);
    }

    @PatchMapping("/{id}/status")
    @Transactional
    public ResponseEntity atualizarStatus(@PathVariable Long id, @RequestBody @Valid DadosAtualizacaoParcela dados) {
        var parcela = repository.getReferenceById(id);
        parcela.atualizarStatus(dados.status());
        return ResponseEntity.ok(new DadosDetalhamentoParcela(parcela));
    }
}
