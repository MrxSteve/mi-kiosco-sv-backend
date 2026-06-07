package com.devplus.mikiosco_sv.presentation.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdatePlanRequest {

    @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
    private String name;

    private String description;

    @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "Formato de precio inválido")
    private java.math.BigDecimal price;

    @Min(value = 1, message = "El límite de usuarios debe ser al menos 1")
    private Integer userLimit;
}
