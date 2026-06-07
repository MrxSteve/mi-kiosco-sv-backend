package com.devplus.mikiosco_sv.presentation.controller;

import com.devplus.mikiosco_sv.application.usecase.orden.*;
import com.devplus.mikiosco_sv.domain.model.OrderStatus;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.*;
import com.devplus.mikiosco_sv.presentation.dto.response.OrderResponse;
import com.devplus.mikiosco_sv.presentation.dto.response.RegisterPaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Órdenes", description = "Creación y gestión del ciclo de vida de las órdenes")
public class OrderController {

    private final ListOrdersUseCase listOrdersUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final CreateOrderUseCase createOrderUseCase;
    private final AddOrderItemUseCase addOrderItemUseCase;
    private final RemoveOrderItemUseCase removeOrderItemUseCase;
    private final ApplyOrderPromotionUseCase applyOrderPromotionUseCase;
    private final RemoveOrderPromotionUseCase removeOrderPromotionUseCase;
    private final RegisterPaymentUseCase registerPaymentUseCase;
    private final SendToKitchenUseCase sendToKitchenUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(summary = "Listar órdenes del día",
            description = "Por defecto retorna las órdenes de hoy. Filtros: ?status=PENDING, ?date=2026-06-06, ?orderNumber=42")
    public ResponseEntity<List<OrderResponse>> list(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long orderNumber,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(listOrdersUseCase.execute(status, date, orderNumber, caller));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(summary = "Ver detalle completo de una orden")
    public ResponseEntity<OrderResponse> get(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(getOrderUseCase.execute(id, caller));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(summary = "Crear nueva orden",
            description = "Puede ser anónima, con nombre libre o vinculada a un cliente registrado.")
    public ResponseEntity<OrderResponse> create(
            @Valid @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createOrderUseCase.execute(request, caller));
    }

    @PostMapping("/{id}/items")
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(summary = "Agregar item a la orden",
            description = "Solo permitido antes de enviar a cocina. Calcula line_subtotal en el backend.")
    public ResponseEntity<OrderResponse> addItem(
            @PathVariable UUID id,
            @Valid @RequestBody AddOrderItemRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addOrderItemUseCase.execute(id, request, caller));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(summary = "Quitar item de la orden")
    public ResponseEntity<OrderResponse> removeItem(
            @PathVariable UUID id,
            @PathVariable UUID itemId,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(removeOrderItemUseCase.execute(id, itemId, caller));
    }

    @PostMapping("/{id}/promotion")
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(summary = "Aplicar promoción a la orden")
    public ResponseEntity<OrderResponse> applyPromotion(
            @PathVariable UUID id,
            @Valid @RequestBody ApplyOrderPromotionRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(applyOrderPromotionUseCase.execute(id, request, caller));
    }

    @DeleteMapping("/{id}/promotion")
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(summary = "Quitar promoción de la orden")
    public ResponseEntity<OrderResponse> removePromotion(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(removeOrderPromotionUseCase.execute(id, caller));
    }

    @PostMapping("/{id}/payment")
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(summary = "Registrar pago en efectivo",
            description = "Valida que amountReceived >= total. Incrementa uses_count si hay promoción.")
    public ResponseEntity<RegisterPaymentResponse> payment(
            @PathVariable UUID id,
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registerPaymentUseCase.execute(id, request, caller));
    }

    @PostMapping("/{id}/send-to-kitchen")
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(summary = "Enviar orden a cocina",
            description = "Bloquea modificaciones. La orden queda visible en la pantalla de cocina.")
    public ResponseEntity<OrderResponse> sendToKitchen(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(sendToKitchenUseCase.execute(id, caller));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(summary = "Cancelar orden", description = "Solo es posible antes de enviar a cocina.")
    public ResponseEntity<OrderResponse> cancel(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(cancelOrderUseCase.execute(id, caller));
    }
}
