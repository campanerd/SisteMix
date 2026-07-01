package org.example.installment.web;

import jakarta.validation.Valid;
import org.example.installment.dto.InstallmentResponse;
import org.example.installment.dto.InstallmentSummary;
import org.example.installment.dto.UpdateInstallmentStatusRequest;
import org.example.installment.enums.InstallmentStatus;
import org.example.installment.service.InstallmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("installments")
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
