package com.devplus.mikiosco_sv.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OnboardingRequest {

    // --- Datos del comedor ---
    @NotBlank(message = "El nombre del comedor es obligatorio")
    @Size(max = 150)
    private String comedorName;

    @Size(max = 200)
    private String legalName;

    @NotBlank(message = "El email del comedor es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @Size(max = 30)
    private String phone;

    @Size(max = 255)
    private String address;

    private String timezone;

    // --- Plan de suscripción ---
    @NotBlank(message = "El código de plan es obligatorio")
    private String planCode = "standard";

    // --- Datos del administrador ---
    @NotBlank(message = "El nombre del administrador es obligatorio")
    @Size(max = 150)
    private String adminName;

    @NotBlank(message = "El email del administrador es obligatorio")
    @Email(message = "Formato de email inválido")
    private String adminEmail;

    @NotBlank(message = "La contraseña del administrador es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String adminPassword;
}
