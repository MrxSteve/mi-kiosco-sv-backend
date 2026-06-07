package com.devplus.mikiosco_sv.presentation.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ValidatePromotionRequest {

    @NotBlank(message = "El código es obligatorio")
    private String code;

    @NotNull(message = "El subtotal de la orden es obligatorio")
    @DecimalMin(value = "0.01", message = "El subtotal debe ser mayor a 0")
    private BigDecimal orderSubtotal;

    private List<UUID> productIds;
}
