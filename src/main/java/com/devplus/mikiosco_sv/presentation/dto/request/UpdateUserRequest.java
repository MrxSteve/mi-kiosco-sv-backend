package com.devplus.mikiosco_sv.presentation.dto.request;

import com.devplus.mikiosco_sv.domain.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateUserRequest {

    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
    private String fullName;

    @Email(message = "Formato de email inválido")
    private String email;

    private UserRole role;
}
