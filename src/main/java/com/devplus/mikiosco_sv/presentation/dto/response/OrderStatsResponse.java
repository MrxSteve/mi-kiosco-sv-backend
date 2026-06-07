package com.devplus.mikiosco_sv.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class OrderStatsResponse {

    private final LocalDate startDate;
    private final LocalDate endDate;
    private final long totalOrders;
    private final long pendingOrders;
    private final long inPreparationOrders;
    private final long completedOrders;
    private final long deliveredOrders;
    private final long cancelledOrders;
    private final BigDecimal cancellationRate;
}
