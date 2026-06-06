package com.devplus.mikiosco_sv.presentation.controller;

import com.devplus.mikiosco_sv.application.usecase.suscripcion.ListPlansUseCase;
import com.devplus.mikiosco_sv.presentation.dto.response.PlanResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@Tag(name = "Planes", description = "Planes de suscripción disponibles")
public class SuscripcionController {

    private final ListPlansUseCase listPlansUseCase;

    @GetMapping
    @Operation(summary = "Listar planes activos", description = "Endpoint público para ver planes disponibles.")
    @SecurityRequirements
    public ResponseEntity<List<PlanResponse>> list() {
        return ResponseEntity.ok(listPlansUseCase.execute());
    }
}
