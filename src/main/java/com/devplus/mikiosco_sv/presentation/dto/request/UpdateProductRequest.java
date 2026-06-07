package com.devplus.mikiosco_sv.presentation.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UpdateProductRequest {

    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    private String name;

    private String description;

    @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "Formato de precio inválido")
    private BigDecimal price;

    private UUID categoryId;

    private String imageUrl;
}
