package org.siste.mix.installment.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.siste.mix.installment.dto.InstallmentResponse;
import org.siste.mix.installment.dto.InstallmentSummary;
import org.siste.mix.installment.dto.UpdateInstallmentStatusRequest;
import org.siste.mix.installment.enums.InstallmentStatus;
import org.siste.mix.installment.usecase.FindInstallmentByIdUseCase;
import org.siste.mix.installment.usecase.ListInstallmentsByOrderUseCase;
import org.siste.mix.installment.usecase.ListInstallmentsUseCase;
import org.siste.mix.installment.usecase.UpdateInstallmentStatusUseCase;
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
    private ListInstallmentsUseCase listInstallmentsUseCase;

    @Autowired
    private FindInstallmentByIdUseCase findInstallmentByIdUseCase;

    @Autowired
    private ListInstallmentsByOrderUseCase listInstallmentsByOrderUseCase;

    @Autowired
    private UpdateInstallmentStatusUseCase updateInstallmentStatusUseCase;

    @GetMapping
    @Operation(summary = "Listar parcelas com filtros opcionais")
    public ResponseEntity<List<InstallmentSummary>> list(
            @RequestParam(required = false) InstallmentStatus status,
            @RequestParam(required = false) LocalDate dueDateFrom,
            @RequestParam(required = false) LocalDate dueDateTo,
            @RequestParam(required = false) BigDecimal amountMin,
            @RequestParam(required = false) BigDecimal amountMax,
            @RequestParam(required = false, defaultValue = "false") boolean showAll
    ) {
        return ResponseEntity.ok(listInstallmentsUseCase.list(status, dueDateFrom, dueDateTo, amountMin, amountMax, showAll));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Listar uma parcela por id")
    public ResponseEntity<InstallmentResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(findInstallmentByIdUseCase.findById(id));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Listar parcelas de um pedido")
    public ResponseEntity<List<InstallmentSummary>> listByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(listInstallmentsByOrderUseCase.listByOrder(orderId));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar o status de uma parcela")
    public ResponseEntity<InstallmentResponse> updateStatus(@PathVariable Long id, @RequestBody @Valid UpdateInstallmentStatusRequest data) {
        return ResponseEntity.ok(updateInstallmentStatusUseCase.updateStatus(id, data));
    }
}
