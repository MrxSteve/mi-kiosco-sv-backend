package com.devplus.mikiosco_sv.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class ConfiguracionComedorResponse {

    private final String currencySymbol;
    private final String currencyCode;
    private final String dateFormat;
    private final String timeFormat;
    private final LocalTime openingTime;
    private final LocalTime closingTime;
    private final int maxLoginAttempts;
    private final int lockoutMinutes;
    private final String ticketFooter;
}
