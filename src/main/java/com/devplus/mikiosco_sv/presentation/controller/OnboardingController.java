package com.devplus.mikiosco_sv.presentation.controller;

import com.devplus.mikiosco_sv.application.usecase.comedor.OnboardingUseCase;
import com.devplus.mikiosco_sv.presentation.dto.request.OnboardingRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.OnboardingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
@Tag(name = "Onboarding", description = "Registro de nuevos comedores")
public class OnboardingController {

    private final OnboardingUseCase onboardingUseCase;

    @PostMapping
    @Operation(
            summary = "Registrar nuevo comedor",
            description = "Crea atómicamente: comedor, configuración por defecto, "
                    + "usuario administrador y suscripción activa. "
                    + "El pago es simulado — confirmar cobro manualmente con la referencia retornada."
    )
    @SecurityRequirements
    public ResponseEntity<OnboardingResponse> register(@Valid @RequestBody OnboardingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(onboardingUseCase.execute(request));
    }
}
