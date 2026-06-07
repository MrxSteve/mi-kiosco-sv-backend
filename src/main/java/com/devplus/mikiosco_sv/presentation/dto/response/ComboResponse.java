package com.devplus.mikiosco_sv.presentation.dto.response;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class ComboResponse {

    private final UUID id;
    private final String label;
    private final int comboQty;
    private final BigDecimal comboPrice;
    private final GenericStatus status;
}
