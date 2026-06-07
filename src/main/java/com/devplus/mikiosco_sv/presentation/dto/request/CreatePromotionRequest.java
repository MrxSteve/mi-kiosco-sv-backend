package com.devplus.mikiosco_sv.presentation.dto.request;

import com.devplus.mikiosco_sv.domain.model.DiscountType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class CreatePromotionRequest {

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 60, message = "El código no puede superar 60 caracteres")
    private String code;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    private String name;

    @NotNull(message = "El tipo de descuento es obligatorio")
    private DiscountType discountKind;

    @NotNull(message = "El valor del descuento es obligatorio")
    @DecimalMin(value = "0.00", message = "El valor del descuento no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "Formato de descuento inválido")
    private BigDecimal discountValue;

    private OffsetDateTime startsAt;

    private OffsetDateTime endsAt;

    @Min(value = 1, message = "El máximo de usos debe ser al menos 1")
    private Integer maxUses;

    @NotNull(message = "El campo appliesToAll es obligatorio")
    private Boolean appliesToAll;

    private List<UUID> productIds;
}
