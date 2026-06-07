package com.devplus.mikiosco_sv.presentation.controller;

import com.devplus.mikiosco_sv.application.usecase.promocion.*;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.CreatePromotionRequest;
import com.devplus.mikiosco_sv.presentation.dto.request.UpdatePromotionRequest;
import com.devplus.mikiosco_sv.presentation.dto.request.ValidatePromotionRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.PromotionResponse;
import com.devplus.mikiosco_sv.presentation.dto.response.ValidatePromotionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Promociones", description = "Gestión y validación de promociones y descuentos")
public class PromotionController {

    private final ListPromotionsUseCase listPromotionsUseCase;
    private final CreatePromotionUseCase createPromotionUseCase;
    private final UpdatePromotionUseCase updatePromotionUseCase;
    private final TogglePromotionStatusUseCase togglePromotionStatusUseCase;
    private final ValidatePromotionUseCase validatePromotionUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(summary = "Listar promociones del comedor")
    public ResponseEntity<List<PromotionResponse>> list(
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(listPromotionsUseCase.execute(caller));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear promoción")
    public ResponseEntity<PromotionResponse> create(
            @Valid @RequestBody CreatePromotionRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createPromotionUseCase.execute(request, caller));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Editar promoción")
    public ResponseEntity<PromotionResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePromotionRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(updatePromotionUseCase.execute(id, request, caller));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activar / desactivar promoción")
    public ResponseEntity<PromotionResponse> toggleStatus(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(togglePromotionStatusUseCase.execute(id, caller));
    }

    @PostMapping("/validate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(summary = "Validar código de promoción",
            description = "Verifica si el código es aplicable a la orden. No incrementa el contador de usos.")
    public ResponseEntity<ValidatePromotionResponse> validate(
            @Valid @RequestBody ValidatePromotionRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(validatePromotionUseCase.execute(request, caller));
    }
}
