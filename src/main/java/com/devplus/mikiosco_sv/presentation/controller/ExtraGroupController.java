package com.devplus.mikiosco_sv.presentation.controller;

import com.devplus.mikiosco_sv.application.usecase.catalogo.*;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.CreateExtraGroupRequest;
import com.devplus.mikiosco_sv.presentation.dto.request.UpdateExtraGroupRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.ExtraGroupResponse;
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
@RequestMapping("/api/extra-groups")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Catálogo - Grupos de Extras", description = "Agrupaciones de extras (ej: Bebidas, Condimentos)")
public class ExtraGroupController {

    private final ListExtraGroupsUseCase listExtraGroupsUseCase;
    private final CreateExtraGroupUseCase createExtraGroupUseCase;
    private final UpdateExtraGroupUseCase updateExtraGroupUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(summary = "Listar grupos de extras")
    public ResponseEntity<List<ExtraGroupResponse>> list(
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(listExtraGroupsUseCase.execute(caller));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear grupo de extras")
    public ResponseEntity<ExtraGroupResponse> create(
            @Valid @RequestBody CreateExtraGroupRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createExtraGroupUseCase.execute(request, caller));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Editar grupo de extras")
    public ResponseEntity<ExtraGroupResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateExtraGroupRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(updateExtraGroupUseCase.execute(id, request, caller));
    }
}
