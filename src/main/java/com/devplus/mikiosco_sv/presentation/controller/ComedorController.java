package com.devplus.mikiosco_sv.presentation.controller;

import com.devplus.mikiosco_sv.application.usecase.comedor.*;
import com.devplus.mikiosco_sv.application.usecase.suscripcion.GetSuscripcionActivaUseCase;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.UpdateComedorRequest;
import com.devplus.mikiosco_sv.presentation.dto.request.UpdateConfiguracionRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.ComedorResponse;
import com.devplus.mikiosco_sv.presentation.dto.response.ConfiguracionComedorResponse;
import com.devplus.mikiosco_sv.presentation.dto.response.SubscriptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comedor")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Comedor", description = "Gestión del comedor autenticado")
public class ComedorController {

    private final GetComedorUseCase getComedorUseCase;
    private final UpdateComedorUseCase updateComedorUseCase;
    private final GetConfiguracionUseCase getConfiguracionUseCase;
    private final UpdateConfiguracionUseCase updateConfiguracionUseCase;
    private final GetSuscripcionActivaUseCase getSuscripcionActivaUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE', 'KITCHEN')")
    @Operation(summary = "Ver datos del comedor autenticado")
    public ResponseEntity<ComedorResponse> get(@AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(getComedorUseCase.execute(caller));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Editar datos del comedor",
            description = "Solo los campos enviados se actualizan (null = mantener valor actual).")
    public ResponseEntity<ComedorResponse> update(
            @Valid @RequestBody UpdateComedorRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(updateComedorUseCase.execute(request, caller));
    }

    @GetMapping("/config")
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE', 'KITCHEN')")
    @Operation(summary = "Ver configuración del comedor",
            description = "Moneda, horario, intentos de login, pie de ticket, etc.")
    public ResponseEntity<ConfiguracionComedorResponse> getConfig(
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(getConfiguracionUseCase.execute(caller));
    }

    @PutMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Editar configuración del comedor",
            description = "Solo los campos enviados se actualizan.")
    public ResponseEntity<ConfiguracionComedorResponse> updateConfig(
            @Valid @RequestBody UpdateConfiguracionRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(updateConfiguracionUseCase.execute(request, caller));
    }

    @GetMapping("/subscription")
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE', 'KITCHEN')")
    @Operation(summary = "Ver suscripción activa del comedor")
    public ResponseEntity<SubscriptionResponse> subscription(
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(getSuscripcionActivaUseCase.execute(caller));
    }
}
