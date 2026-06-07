package com.devplus.mikiosco_sv.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class SalesReportResponse {

    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BigDecimal totalRevenue;
    private final long orderCount;
    private final BigDecimal avgTicket;
    private final List<CategorySalesResponse> byCategory;
    private final List<PeakHourResponse> peakHours;
}
