package com.devplus.mikiosco_sv.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ValidatePromotionResponse {

    private final boolean valid;
    private final BigDecimal discountAmount;
    private final String message;
}
