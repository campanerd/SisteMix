package org.example.controller;

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
    private ParcelaService service;

    @GetMapping
    public ResponseEntity<List<InstallmentSummary>> list(
            @RequestParam(required = false) InstallmentStatus status,
            @RequestParam(required = false) LocalDate vencimentoInicio,
            @RequestParam(required = false) LocalDate vencimentoFim,
            @RequestParam(required = false) BigDecimal valorMin,
            @RequestParam(required = false) BigDecimal valorMax
    ) {
        return ResponseEntity.ok(service.list(status, vencimentoInicio, vencimentoFim, valorMin, valorMax));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstallmentResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/pedido/{orderId}")
    public ResponseEntity<List<InstallmentSummary>> listByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(service.listByOrder(orderId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<InstallmentResponse> updateStatus(@PathVariable Long id, @RequestBody @Valid UpdateInstallmentStatusRequest data) {
        return ResponseEntity.ok(service.updateStatus(id, data));
    }
}
