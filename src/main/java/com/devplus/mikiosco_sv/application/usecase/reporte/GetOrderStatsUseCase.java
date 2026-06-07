package com.devplus.mikiosco_sv.application.usecase.reporte;

import com.devplus.mikiosco_sv.infrastructure.persistence.report.ReportQueryService;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.OrderStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class GetOrderStatsUseCase {

    private final ReportQueryService queryService;

    public OrderStatsResponse execute(LocalDate startDate, LocalDate endDate, AuthenticatedUser caller) {
        OffsetDateTime from = startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime to   = endDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);

        Object[] row = queryService.getOrderStats(caller.comedorId(), from, to);

        long total      = ((Number) row[0]).longValue();
        long pending    = ((Number) row[1]).longValue();
        long inPrep     = ((Number) row[2]).longValue();
        long completed  = ((Number) row[3]).longValue();
        long delivered  = ((Number) row[4]).longValue();
        long cancelled  = ((Number) row[5]).longValue();

        BigDecimal cancellationRate = total == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(cancelled * 100)
                        .divide(BigDecimal.valueOf(total), 1, RoundingMode.HALF_UP);

        return OrderStatsResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalOrders(total)
                .pendingOrders(pending)
                .inPreparationOrders(inPrep)
                .completedOrders(completed)
                .deliveredOrders(delivered)
                .cancelledOrders(cancelled)
                .cancellationRate(cancellationRate)
                .build();
    }
}
