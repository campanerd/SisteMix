package org.siste.mix.dashboard.web;

import org.siste.mix.dashboard.dto.DashboardSummaryResponse;
import org.siste.mix.dashboard.usecase.GetDashboardSummaryUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private GetDashboardSummaryUseCase getDashboardSummaryUseCase;

    @InjectMocks
    private DashboardController controller;

    @Test
    void should_return_dashboard_summary_with_200() {
        var summary = new DashboardSummaryResponse(10, 5, 3, 2, new BigDecimal("500.00"), new BigDecimal("250.00"));

        // WHEN
        when(getDashboardSummaryUseCase.get()).thenReturn(summary);

        // ASSERT
        var response = controller.summary();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10, response.getBody().activeOrdersCount());
        assertEquals(5, response.getBody().paidCount());
        assertEquals(3, response.getBody().pendingCount());
        assertEquals(2, response.getBody().overdueCount());

        // InOrder
        InOrder inOrder = inOrder(getDashboardSummaryUseCase);
        inOrder.verify(getDashboardSummaryUseCase).get();
    }
}
