package com.devplus.mikiosco_sv.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class DashboardSummaryResponse {

    private final LocalDate date;
    private final BigDecimal todaySales;
    private final long todayPaidOrders;
    private final long todayTotalOrders;
    private final BigDecimal avgTicketToday;
    private final long activeOrdersInKitchen;
    private final BigDecimal allTimeTotalRevenue;
}
