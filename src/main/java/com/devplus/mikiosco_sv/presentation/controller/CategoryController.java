package com.devplus.mikiosco_sv.presentation.controller;

import com.devplus.mikiosco_sv.application.usecase.catalogo.*;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.CreateCategoryRequest;
import com.devplus.mikiosco_sv.presentation.dto.request.UpdateCategoryRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.CategoryResponse;
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
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Catálogo - Categorías", description = "Gestión de categorías del catálogo")
public class CategoryController {

    private final ListCategoriesUseCase listCategoriesUseCase;
    private final CreateCategoryUseCase createCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final ToggleCategoryStatusUseCase toggleCategoryStatusUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(summary = "Listar categorías", description = "Por defecto retorna todas. Usar ?active=true para solo las activas.")
    public ResponseEntity<List<CategoryResponse>> list(
            @RequestParam(defaultValue = "false") boolean active,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(listCategoriesUseCase.execute(active, caller));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear categoría")
    public ResponseEntity<CategoryResponse> create(
            @Valid @RequestBody CreateCategoryRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createCategoryUseCase.execute(request, caller));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Editar categoría")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(updateCategoryUseCase.execute(id, request, caller));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activar / desactivar categoría")
    public ResponseEntity<CategoryResponse> toggleStatus(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(toggleCategoryStatusUseCase.execute(id, caller));
    }
}
