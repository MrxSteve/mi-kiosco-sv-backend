package com.devplus.mikiosco_sv.presentation.controller;

import com.devplus.mikiosco_sv.application.usecase.cocina.*;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.UpdateOrderStatusRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.KitchenOrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/kitchen/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Cocina", description = "Vista y control de órdenes desde la pantalla de cocina")
public class KitchenController {

    private final ListKitchenOrdersUseCase listKitchenOrdersUseCase;
    private final GetKitchenOrderUseCase getKitchenOrderUseCase;
    private final UpdateKitchenOrderStatusUseCase updateKitchenOrderStatusUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('KITCHEN', 'ADMIN')")
    @Operation(summary = "Listar órdenes activas de cocina",
            description = "Retorna órdenes con status PENDING + IN_PREPARATION que ya fueron enviadas a cocina, "
                    + "ordenadas por tiempo de llegada (más antiguas primero). "
                    + "El frontend puede hacer polling cada 5-10 segundos.")
    public ResponseEntity<List<KitchenOrderResponse>> list(
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(listKitchenOrdersUseCase.execute(caller));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('KITCHEN', 'ADMIN')")
    @Operation(summary = "Ver detalle de una orden en cocina")
    public ResponseEntity<KitchenOrderResponse> get(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(getKitchenOrderUseCase.execute(id, caller));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('KITCHEN', 'ADMIN')")
    @Operation(summary = "Cambiar estado de la orden en cocina",
            description = "Transiciones válidas: PENDING → IN_PREPARATION → COMPLETED. "
                    + "Reversa permitida: IN_PREPARATION → PENDING (error en cocina).")
    public ResponseEntity<KitchenOrderResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(updateKitchenOrderStatusUseCase.execute(id, request, caller));
    }
}
