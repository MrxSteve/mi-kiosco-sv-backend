package com.devplus.mikiosco_sv.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PeakHourResponse {

    private final int hour;
    private final String hourLabel;
    private final long orderCount;
    private final BigDecimal revenue;
}
