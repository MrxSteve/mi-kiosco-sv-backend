package com.devplus.mikiosco_sv.presentation.controller;

import com.devplus.mikiosco_sv.application.usecase.catalogo.*;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.CreateExtraRequest;
import com.devplus.mikiosco_sv.presentation.dto.request.UpdateExtraRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.ExtraResponse;
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
@RequestMapping("/api/extras")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Catálogo - Extras", description = "Extras y complementos de productos (salsas, bebidas, etc.)")
public class ExtraController {

    private final ListExtrasUseCase listExtrasUseCase;
    private final CreateExtraUseCase createExtraUseCase;
    private final UpdateExtraUseCase updateExtraUseCase;
    private final ToggleExtraStatusUseCase toggleExtraStatusUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(summary = "Listar extras", description = "Filtros opcionales: ?groupId=UUID y ?active=true")
    public ResponseEntity<List<ExtraResponse>> list(
            @RequestParam(required = false) UUID groupId,
            @RequestParam(defaultValue = "false") boolean active,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(listExtrasUseCase.execute(groupId, active, caller));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear extra")
    public ResponseEntity<ExtraResponse> create(
            @Valid @RequestBody CreateExtraRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createExtraUseCase.execute(request, caller));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Editar extra")
    public ResponseEntity<ExtraResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateExtraRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(updateExtraUseCase.execute(id, request, caller));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activar / desactivar extra")
    public ResponseEntity<ExtraResponse> toggleStatus(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(toggleExtraStatusUseCase.execute(id, caller));
    }
}
