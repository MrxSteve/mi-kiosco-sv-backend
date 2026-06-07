package com.devplus.mikiosco_sv.presentation.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExtendSuscripcionRequest {

    @NotNull(message = "La nueva fecha de fin es obligatoria")
    @Future(message = "La nueva fecha de fin debe ser futura")
    private LocalDate newEndDate;

    private String notes;
}
