package com.devplus.mikiosco_sv.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateClientRequest {

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    private String fullName;

    @Email(message = "El email no tiene un formato válido")
    @Size(max = 150)
    private String email;

    @Size(max = 30, message = "El teléfono no puede superar 30 caracteres")
    private String phone;

    @Size(max = 255, message = "La dirección no puede superar 255 caracteres")
    private String address;

    private String notes;
}
