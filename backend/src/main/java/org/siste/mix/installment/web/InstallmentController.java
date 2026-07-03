package org.siste.mix.installment.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.siste.mix.installment.dto.InstallmentResponse;
import org.siste.mix.installment.dto.InstallmentSummary;
import org.siste.mix.installment.dto.UpdateInstallmentStatusRequest;
import org.siste.mix.installment.enums.InstallmentStatus;
import org.siste.mix.installment.service.InstallmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/installments")
@Tag(name = "Parcelas", description = "Cadastro e consulta de parcelas")
public class InstallmentController {

    @Autowired
    private InstallmentService service;

    @GetMapping
    public ResponseEntity<List<InstallmentSummary>> list(
            @RequestParam(required = false) InstallmentStatus status,
            @RequestParam(required = false) LocalDate dueDateFrom,
            @RequestParam(required = false) LocalDate dueDateTo,
            @RequestParam(required = false) BigDecimal amountMin,
            @RequestParam(required = false) BigDecimal amountMax
    ) {
        return ResponseEntity.ok(service.list(status, dueDateFrom, dueDateTo, amountMin, amountMax));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstallmentResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<InstallmentSummary>> listByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(service.listByOrder(orderId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<InstallmentResponse> updateStatus(@PathVariable Long id, @RequestBody @Valid UpdateInstallmentStatusRequest data) {
        return ResponseEntity.ok(service.updateStatus(id, data));
    }
}
