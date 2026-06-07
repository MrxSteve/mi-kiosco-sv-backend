package com.devplus.mikiosco_sv.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Builder
public class TopClientResponse {

    private final String clientName;
    private final String customerCode;
    private final long orderCount;
    private final BigDecimal totalSpent;
    private final OffsetDateTime lastOrderAt;
}
