package org.example.parcela.web;

import jakarta.validation.Valid;
import org.example.parcela.dto.DadosAtualizacaoParcela;
import org.example.parcela.dto.DadosDetalhamentoParcela;
import org.example.parcela.dto.DadosListagemParcela;
import org.example.parcela.enums.StatusParcela;
import org.example.parcela.service.ParcelaService;
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
    private ParcelaService service;

    @GetMapping
    public ResponseEntity<List<DadosListagemParcela>> list(
            @RequestParam(required = false) StatusParcela status,
            @RequestParam(required = false) LocalDate vencimentoInicio,
            @RequestParam(required = false) LocalDate vencimentoFim,
            @RequestParam(required = false) BigDecimal valorMin,
            @RequestParam(required = false) BigDecimal valorMax
    ) {
        return ResponseEntity.ok(service.list(status, vencimentoInicio, vencimentoFim, valorMin, valorMax));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoParcela> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<List<DadosListagemParcela>> listByOrder(@PathVariable Long idPedido) {
        return ResponseEntity.ok(service.listByOrder(idPedido));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DadosDetalhamentoParcela> updateStatus(@PathVariable Long id, @RequestBody @Valid DadosAtualizacaoParcela dados) {
        return ResponseEntity.ok(service.updateStatus(id, dados));
    }
}
