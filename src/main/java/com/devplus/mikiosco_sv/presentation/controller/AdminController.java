package com.devplus.mikiosco_sv.presentation.controller;

import com.devplus.mikiosco_sv.application.usecase.comedor.GetAdminStatsUseCase;
import com.devplus.mikiosco_sv.application.usecase.comedor.ListComedoresUseCase;
import com.devplus.mikiosco_sv.application.usecase.comedor.ToggleComedorStatusUseCase;
import com.devplus.mikiosco_sv.application.usecase.superadmin.*;
import com.devplus.mikiosco_sv.domain.model.SubscriptionStatus;
import com.devplus.mikiosco_sv.presentation.dto.request.ExtendSuscripcionRequest;
import com.devplus.mikiosco_sv.presentation.dto.request.UpdatePlanRequest;
import com.devplus.mikiosco_sv.presentation.dto.request.UpdateSuscripcionStatusRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin", description = "Endpoints exclusivos para super_admin")
public class AdminController {

    private final ListComedoresUseCase listComedoresUseCase;
    private final ToggleComedorStatusUseCase toggleComedorStatusUseCase;
    private final GetAdminStatsUseCase getAdminStatsUseCase;
    private final GetComedorAdminDetailUseCase getComedorAdminDetailUseCase;

    private final ListPlansWithStatsUseCase listPlansWithStatsUseCase;
    private final UpdatePlanUseCase updatePlanUseCase;
    private final TogglePlanStatusUseCase togglePlanStatusUseCase;

    private final ListSuscripcionesUseCase listSuscripcionesUseCase;
    private final GetSubscriptionRevenueUseCase getSubscriptionRevenueUseCase;
    private final UpdateSuscripcionStatusUseCase updateSuscripcionStatusUseCase;
    private final ExtendSuscripcionUseCase extendSuscripcionUseCase;

    // ── Comedores ────────────────────────────────────────────────────────────

    @GetMapping("/comedores")
    @Operation(summary = "Listar todos los comedores")
    public ResponseEntity<List<ComedorResponse>> listComedores() {
        return ResponseEntity.ok(listComedoresUseCase.execute());
    }

    @GetMapping("/comedores/{id}")
    @Operation(summary = "Detalle de comedor con suscripción y conteo de usuarios")
    public ResponseEntity<AdminComedorDetailResponse> getComedor(@PathVariable UUID id) {
        return ResponseEntity.ok(getComedorAdminDetailUseCase.execute(id));
    }

    @PatchMapping("/comedores/{id}/status")
    @Operation(summary = "Activar / desactivar un comedor",
            description = "Al desactivar un comedor sus usuarios no podrán iniciar sesión.")
    public ResponseEntity<ComedorResponse> toggleComedorStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(toggleComedorStatusUseCase.execute(id));
    }

    @GetMapping("/stats")
    @Operation(summary = "Estadísticas generales del sistema")
    public ResponseEntity<AdminStatsResponse> stats() {
        return ResponseEntity.ok(getAdminStatsUseCase.execute());
    }

    // ── Planes ───────────────────────────────────────────────────────────────

    @GetMapping("/plans")
    @Operation(summary = "Listar planes con métricas de suscriptores y revenue")
    public ResponseEntity<List<AdminPlanResponse>> listPlans() {
        return ResponseEntity.ok(listPlansWithStatsUseCase.execute());
    }

    @PutMapping("/plans/{id}")
    @Operation(summary = "Editar plan",
            description = "Campos editables: nombre, descripción, precio, límite de usuarios. "
                    + "El cambio de precio no afecta suscripciones ya existentes.")
    public ResponseEntity<AdminPlanResponse> updatePlan(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePlanRequest request) {
        return ResponseEntity.ok(updatePlanUseCase.execute(id, request));
    }

    @PatchMapping("/plans/{id}/status")
    @Operation(summary = "Activar / desactivar plan",
            description = "Desactivar un plan impide que nuevos comedores lo seleccionen, "
                    + "pero no afecta las suscripciones activas existentes.")
    public ResponseEntity<AdminPlanResponse> togglePlanStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(togglePlanStatusUseCase.execute(id));
    }

    // ── Suscripciones ────────────────────────────────────────────────────────

    @GetMapping("/subscriptions")
    @Operation(summary = "Listar suscripciones",
            description = "Todas las suscripciones del sistema. Filtrar con ?status=ACTIVE|EXPIRED|SUSPENDED|CANCELLED")
    public ResponseEntity<List<AdminSuscripcionResponse>> listSubscriptions(
            @RequestParam(required = false) SubscriptionStatus status) {
        return ResponseEntity.ok(listSuscripcionesUseCase.execute(status));
    }

    @GetMapping("/subscriptions/revenue")
    @Operation(summary = "Métricas de revenue de suscripciones",
            description = "MRR, ARR y desglose por plan. Basado en suscripciones activas actuales.")
    public ResponseEntity<SubscriptionRevenueResponse> subscriptionRevenue() {
        return ResponseEntity.ok(getSubscriptionRevenueUseCase.execute());
    }

    @PatchMapping("/subscriptions/{id}/status")
    @Operation(summary = "Cambiar estado de una suscripción",
            description = "Estados válidos: ACTIVE, EXPIRED, SUSPENDED, CANCELLED. "
                    + "Úsalo para suspender comedores morosos o reactivar suscripciones expiradas.")
    public ResponseEntity<AdminSuscripcionResponse> updateSubscriptionStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSuscripcionStatusRequest request) {
        return ResponseEntity.ok(updateSuscripcionStatusUseCase.execute(id, request));
    }

    @PutMapping("/subscriptions/{id}/extend")
    @Operation(summary = "Extender suscripción",
            description = "Cambia la fecha de fin de la suscripción. Útil para períodos de gracia o ajustes manuales.")
    public ResponseEntity<AdminSuscripcionResponse> extendSubscription(
            @PathVariable UUID id,
            @Valid @RequestBody ExtendSuscripcionRequest request) {
        return ResponseEntity.ok(extendSuscripcionUseCase.execute(id, request));
    }
}
