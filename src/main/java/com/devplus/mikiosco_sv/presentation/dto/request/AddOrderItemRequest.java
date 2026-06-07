package com.devplus.mikiosco_sv.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AddOrderItemRequest {

    @NotNull(message = "El producto es obligatorio")
    private UUID productoId;

    private UUID comboId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer quantity;

    private List<UUID> extraIds;

    private String notes;
}
