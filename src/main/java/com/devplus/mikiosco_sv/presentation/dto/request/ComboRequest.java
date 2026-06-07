package com.devplus.mikiosco_sv.presentation.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ComboRequest {

    @NotBlank(message = "El label es obligatorio")
    @Size(max = 100, message = "El label no puede superar 100 caracteres")
    private String label;

    @NotNull(message = "La cantidad del combo es obligatoria")
    @Min(value = 2, message = "El combo debe incluir al menos 2 unidades")
    private Integer comboQty;

    @NotNull(message = "El precio del combo es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio del combo debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "Formato de precio inválido")
    private BigDecimal comboPrice;
}
