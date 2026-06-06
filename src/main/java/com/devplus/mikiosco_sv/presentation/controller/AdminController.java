package com.devplus.mikiosco_sv.presentation.controller;

import com.devplus.mikiosco_sv.application.usecase.comedor.GetAdminStatsUseCase;
import com.devplus.mikiosco_sv.application.usecase.comedor.ListComedoresUseCase;
import com.devplus.mikiosco_sv.application.usecase.comedor.ToggleComedorStatusUseCase;
import com.devplus.mikiosco_sv.presentation.dto.response.AdminStatsResponse;
import com.devplus.mikiosco_sv.presentation.dto.response.ComedorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin", description = "Endpoints exclusivos para super_admin")
public class AdminController {

    private final ListComedoresUseCase listComedoresUseCase;
    private final ToggleComedorStatusUseCase toggleComedorStatusUseCase;
    private final GetAdminStatsUseCase getAdminStatsUseCase;

    @GetMapping("/comedores")
    @Operation(summary = "Listar todos los comedores")
    public ResponseEntity<List<ComedorResponse>> listComedores() {
        return ResponseEntity.ok(listComedoresUseCase.execute());
    }

    @PatchMapping("/comedores/{id}/status")
    @Operation(summary = "Activar / desactivar un comedor",
            description = "Al desactivar un comedor sus usuarios no podrán iniciar sesión.")
    public ResponseEntity<ComedorResponse> toggleComedorStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(toggleComedorStatusUseCase.execute(id));
    }

    @GetMapping("/stats")
    @Operation(summary = "Estadísticas generales del sistema")
    public ResponseEntity<AdminStatsResponse> stats() {
        return ResponseEntity.ok(getAdminStatsUseCase.execute());
    }
}
