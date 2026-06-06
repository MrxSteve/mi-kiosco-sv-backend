package com.devplus.mikiosco_sv.presentation.controller;

import com.devplus.mikiosco_sv.application.usecase.auth.LoginUseCase;
import com.devplus.mikiosco_sv.application.usecase.usuario.GetMyProfileUseCase;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.LoginRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.LoginResponse;
import com.devplus.mikiosco_sv.presentation.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Autenticación")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final GetMyProfileUseCase getMyProfileUseCase;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión",
            description = "Retorna un JWT válido por 8 horas. Omitir comedorId para super_admin.")
    @SecurityRequirements
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginUseCase.execute(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Mi perfil", description = "Retorna el perfil del usuario autenticado.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal AuthenticatedUser caller) {
        return ResponseEntity.ok(getMyProfileUseCase.execute(caller));
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión",
            description = "JWT es stateless: elimina el token en el cliente. "
                    + "Invalidación real con blacklist se implementará en versión futura.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }
}
