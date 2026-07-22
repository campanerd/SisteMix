package org.siste.mix.dashboard.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.siste.mix.dashboard.dto.DashboardSummaryResponse;
import org.siste.mix.dashboard.usecase.GetDashboardSummaryUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard", description = "Indicadores de acompanhamento")
public class DashboardController {

    @Autowired
    private GetDashboardSummaryUseCase getDashboardSummaryUseCase;

    @GetMapping("/summary")
    @Operation(summary = "Resumo de indicadores para a tela de Acompanhamento")
    public ResponseEntity<DashboardSummaryResponse> summary() {
        return ResponseEntity.ok(getDashboardSummaryUseCase.get());
    }
}
