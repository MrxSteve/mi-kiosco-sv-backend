package com.devplus.mikiosco_sv.application.usecase.dashboard;

import com.devplus.mikiosco_sv.infrastructure.persistence.report.ReportQueryService;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.DashboardSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class GetDashboardSummaryUseCase {

    private final ReportQueryService queryService;

    public DashboardSummaryResponse execute(AuthenticatedUser caller) {
        var comedorId = caller.comedorId();
        var today = LocalDate.now(ZoneOffset.UTC);
        OffsetDateTime from = today.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime to   = from.plusDays(1);

        Object[] salesRow = queryService.getDashboardSalesSummary(comedorId, from, to);
        BigDecimal todaySales     = (BigDecimal) salesRow[0];
        long       todayPaidOrders = ((Number) salesRow[1]).longValue();

        long todayTotalOrders    = queryService.countTodayOrders(comedorId, from, to);
        long activeOrdersKitchen = queryService.countActiveKitchenOrders(comedorId);
        BigDecimal allTimeRevenue = queryService.getTotalRevenue(comedorId);

        BigDecimal avgTicket = todayPaidOrders == 0
                ? BigDecimal.ZERO
                : todaySales.divide(BigDecimal.valueOf(todayPaidOrders), 2, RoundingMode.HALF_UP);

        return DashboardSummaryResponse.builder()
                .date(today)
                .todaySales(todaySales)
                .todayPaidOrders(todayPaidOrders)
                .todayTotalOrders(todayTotalOrders)
                .avgTicketToday(avgTicket)
                .activeOrdersInKitchen(activeOrdersKitchen)
                .allTimeTotalRevenue(allTimeRevenue)
                .build();
    }
}
