package com.devplus.mikiosco_sv.presentation.controller;

import com.devplus.mikiosco_sv.application.usecase.catalogo.*;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.*;
import com.devplus.mikiosco_sv.presentation.dto.response.ProductResponse;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Catálogo - Productos", description = "Gestión de productos, combos y asignación de extras")
public class ProductController {

    private final ListProductsUseCase listProductsUseCase;
    private final GetProductUseCase getProductUseCase;
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final ToggleProductStatusUseCase toggleProductStatusUseCase;
    private final AddProductComboUseCase addProductComboUseCase;
    private final UpdateProductComboUseCase updateProductComboUseCase;
    private final DeleteProductComboUseCase deleteProductComboUseCase;
    private final AssignProductExtrasUseCase assignProductExtrasUseCase;
    private final RemoveProductExtraUseCase removeProductExtraUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(summary = "Listar productos", description = "Filtros opcionales: ?categoryId=UUID y ?active=true. Incluye combos y extras agrupados.")
    public ResponseEntity<List<ProductResponse>> list(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue = "false") boolean active,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(listProductsUseCase.execute(categoryId, active, caller));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(summary = "Ver producto con combos y extras")
    public ResponseEntity<ProductResponse> get(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(getProductUseCase.execute(id, caller));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear producto")
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody CreateProductRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createProductUseCase.execute(request, caller));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Editar producto")
    public ResponseEntity<ProductResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(updateProductUseCase.execute(id, request, caller));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activar / desactivar producto")
    public ResponseEntity<ProductResponse> toggleStatus(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(toggleProductStatusUseCase.execute(id, caller));
    }

    // ── Combos ──────────────────────────────────────────────────────────────

    @PostMapping("/{id}/combos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Agregar combo al producto")
    public ResponseEntity<ProductResponse> addCombo(
            @PathVariable UUID id,
            @Valid @RequestBody ComboRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addProductComboUseCase.execute(id, request, caller));
    }

    @PutMapping("/{id}/combos/{comboId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Editar combo del producto")
    public ResponseEntity<ProductResponse> updateCombo(
            @PathVariable UUID id,
            @PathVariable UUID comboId,
            @Valid @RequestBody ComboRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(updateProductComboUseCase.execute(id, comboId, request, caller));
    }

    @DeleteMapping("/{id}/combos/{comboId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar combo del producto")
    public ResponseEntity<Void> deleteCombo(
            @PathVariable UUID id,
            @PathVariable UUID comboId,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        deleteProductComboUseCase.execute(id, comboId, caller);
        return ResponseEntity.noContent().build();
    }

    // ── Extras ──────────────────────────────────────────────────────────────

    @PostMapping("/{id}/extras")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Asignar extras al producto", description = "Agrega los extras indicados al producto (idempotente).")
    public ResponseEntity<ProductResponse> assignExtras(
            @PathVariable UUID id,
            @Valid @RequestBody AssignExtrasRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(assignProductExtrasUseCase.execute(id, request, caller));
    }

    @DeleteMapping("/{id}/extras/{extraId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Quitar extra del producto")
    public ResponseEntity<Void> removeExtra(
            @PathVariable UUID id,
            @PathVariable UUID extraId,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        removeProductExtraUseCase.execute(id, extraId, caller);
        return ResponseEntity.noContent().build();
    }
}
