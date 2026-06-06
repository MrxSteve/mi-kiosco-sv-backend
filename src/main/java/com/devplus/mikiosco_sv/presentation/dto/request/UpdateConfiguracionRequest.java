package com.devplus.mikiosco_sv.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
public class UpdateConfiguracionRequest {

    @Size(max = 10)
    private String currencySymbol;

    @Size(max = 5)
    private String currencyCode;

    @Size(max = 30)
    private String dateFormat;

    @Size(max = 10)
    private String timeFormat;

    private LocalTime openingTime;

    private LocalTime closingTime;

    @Min(value = 1, message = "El mínimo de intentos permitidos es 1")
    private Integer maxLoginAttempts;

    @Min(value = 0, message = "Los minutos de bloqueo no pueden ser negativos")
    private Integer lockoutMinutes;

    private String ticketFooter;
}
