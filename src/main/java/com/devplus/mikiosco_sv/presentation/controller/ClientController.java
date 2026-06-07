package com.devplus.mikiosco_sv.presentation.controller;

import com.devplus.mikiosco_sv.application.usecase.cliente.*;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.CreateClientRequest;
import com.devplus.mikiosco_sv.presentation.dto.request.UpdateClientRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.ClientDetailResponse;
import com.devplus.mikiosco_sv.presentation.dto.response.ClientOrderHistoryResponse;
import com.devplus.mikiosco_sv.presentation.dto.response.ClientResponse;
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
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Clientes", description = "Registro y consulta de clientes del comedor")
public class ClientController {

    private final ListClientsUseCase listClientsUseCase;
    private final GetClientUseCase getClientUseCase;
    private final CreateClientUseCase createClientUseCase;
    private final UpdateClientUseCase updateClientUseCase;
    private final ToggleClientStatusUseCase toggleClientStatusUseCase;
    private final GetClientOrderHistoryUseCase getClientOrderHistoryUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(summary = "Listar clientes", description = "Usa ?q=texto para buscar por nombre, email o código.")
    public ResponseEntity<List<ClientResponse>> list(
            @RequestParam(required = false) String q,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(listClientsUseCase.execute(q, caller));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(summary = "Ver cliente con estadísticas de órdenes")
    public ResponseEntity<ClientDetailResponse> get(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(getClientUseCase.execute(id, caller));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(summary = "Registrar cliente", description = "El código de cliente (CLI-XXXXXX) se genera automáticamente.")
    public ResponseEntity<ClientResponse> create(
            @Valid @RequestBody CreateClientRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createClientUseCase.execute(request, caller));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(summary = "Editar cliente")
    public ResponseEntity<ClientResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateClientRequest request,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(updateClientUseCase.execute(id, request, caller));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activar / desactivar cliente")
    public ResponseEntity<ClientResponse> toggleStatus(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(toggleClientStatusUseCase.execute(id, caller));
    }

    @GetMapping("/{id}/orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
    @Operation(summary = "Historial de órdenes del cliente")
    public ResponseEntity<List<ClientOrderHistoryResponse>> orderHistory(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(getClientOrderHistoryUseCase.execute(id, caller));
    }
}
