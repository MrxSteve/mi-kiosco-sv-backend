package com.devplus.mikiosco_sv.presentation.controller;

import com.devplus.mikiosco_sv.application.usecase.dashboard.*;
import com.devplus.mikiosco_sv.domain.model.AuditAction;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.AuditLogResponse;
import com.devplus.mikiosco_sv.presentation.dto.response.DashboardSummaryResponse;
import com.devplus.mikiosco_sv.presentation.dto.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Dashboard", description = "Resumen del día y log de auditoría")
public class DashboardController {

    private final GetDashboardSummaryUseCase getDashboardSummaryUseCase;
    private final GetAuditLogUseCase getAuditLogUseCase;

    @GetMapping("/api/dashboard/summary")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Resumen del día",
            description = "Ventas de hoy, órdenes activas en cocina, ticket promedio e ingresos históricos totales.")
    public ResponseEntity<DashboardSummaryResponse> summary(
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(getDashboardSummaryUseCase.execute(caller));
    }

    @GetMapping("/api/audit-log")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Log de auditoría",
            description = "Historial de acciones del sistema. Usa ?action=LOGIN para filtrar por tipo.")
    public ResponseEntity<PageResponse<AuditLogResponse>> auditLog(
            @RequestParam(required = false) AuditAction action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(getAuditLogUseCase.execute(action, page, size, caller));
    }
}
