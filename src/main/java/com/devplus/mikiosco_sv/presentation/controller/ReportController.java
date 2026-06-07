package com.devplus.mikiosco_sv.presentation.controller;

import com.devplus.mikiosco_sv.application.usecase.orden.SearchOrdersUseCase;
import com.devplus.mikiosco_sv.application.usecase.reporte.*;
import com.devplus.mikiosco_sv.domain.model.OrderStatus;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reportes", description = "Reportes analíticos de ventas, productos y clientes")
public class ReportController {

    private final GetSalesReportUseCase getSalesReportUseCase;
    private final GetTopProductsUseCase getTopProductsUseCase;
    private final GetTopClientsUseCase getTopClientsUseCase;
    private final GetOrderStatsUseCase getOrderStatsUseCase;
    private final SearchOrdersUseCase searchOrdersUseCase;

    @GetMapping("/sales")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reporte de ventas",
            description = "Ingresos totales, ticket promedio, ventas por categoría y horas pico. "
                    + "Filtra sobre pagos confirmados (pago.paid_at).")
    public ResponseEntity<SalesReportResponse> sales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(getSalesReportUseCase.execute(startDate, endDate, caller));
    }

    @GetMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Productos más vendidos",
            description = "Ordenados por cantidad vendida. Incluye porcentaje sobre el total de ingresos.")
    public ResponseEntity<List<TopProductResponse>> topProducts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(getTopProductsUseCase.execute(startDate, endDate, limit, caller));
    }

    @GetMapping("/clients")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Clientes más frecuentes",
            description = "Ordenados por total gastado. Solo clientes con cuenta registrada.")
    public ResponseEntity<List<TopClientResponse>> topClients(
            @RequestParam(defaultValue = "10") int limit,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(getTopClientsUseCase.execute(limit, caller));
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Estadísticas de órdenes",
            description = "Conteo por estado y tasa de cancelación en el rango de fechas.")
    public ResponseEntity<OrderStatsResponse> orderStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(getOrderStatsUseCase.execute(startDate, endDate, caller));
    }

    // ── Búsqueda avanzada

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(
            summary = "Buscar órdenes con filtros avanzados",
            description = """
                    Filtros disponibles (todos opcionales, combinables):
                    - `orderNumber` — número exacto de orden
                    - `q` — texto libre: busca en nombre del cliente registrado o en el nombre libre de la orden
                    - `anonymous` — `true` para ver solo órdenes sin cliente ni nombre (completamente anónimas)
                    - `status` — PENDING | IN_PREPARATION | COMPLETED | DELIVERED | CANCELLED
                    - `hasPromotion` — `true` para órdenes con promoción aplicada
                    - `hasDiscount` — `true` para órdenes con descuento real > $0.00
                    - `startDate` / `endDate` — rango de fechas de creación (formato: 2026-06-07)
                    - `page` / `size` — paginación (default: página 0, tamaño 20)
                    """)
    public ResponseEntity<PageResponse<OrderResponse>> search(
            @RequestParam(required = false) Long orderNumber,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean anonymous,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Boolean hasPromotion,
            @RequestParam(required = false) Boolean hasDiscount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(searchOrdersUseCase.execute(
                orderNumber, q, anonymous, status, hasPromotion, hasDiscount,
                startDate, endDate, page, size, caller));
    }
}
