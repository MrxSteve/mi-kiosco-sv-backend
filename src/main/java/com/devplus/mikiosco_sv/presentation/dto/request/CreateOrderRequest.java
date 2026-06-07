package com.devplus.mikiosco_sv.presentation.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateOrderRequest {

    private UUID clienteId;

    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    private String customerName;

    private String notes;
}
