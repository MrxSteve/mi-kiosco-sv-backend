package com.devplus.mikiosco_sv.presentation.dto.request;

import com.devplus.mikiosco_sv.domain.model.DiscountType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class UpdatePromotionRequest {

    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    private String name;

    private DiscountType discountKind;

    @DecimalMin(value = "0.00", message = "El valor del descuento no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "Formato de descuento inválido")
    private BigDecimal discountValue;

    private OffsetDateTime startsAt;

    private OffsetDateTime endsAt;

    @Min(value = 1, message = "El máximo de usos debe ser al menos 1")
    private Integer maxUses;

    private Boolean appliesToAll;

    private List<UUID> productIds;
}
