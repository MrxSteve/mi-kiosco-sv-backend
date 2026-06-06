package com.devplus.mikiosco_sv.presentation.dto.response;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class PlanResponse {

    private final UUID id;
    private final String code;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final String billingCycle;
    private final int userLimit;
    private final GenericStatus status;
}
