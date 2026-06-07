package com.devplus.mikiosco_sv.application.usecase.reporte;

import com.devplus.mikiosco_sv.infrastructure.persistence.report.ReportQueryService;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.CategorySalesResponse;
import com.devplus.mikiosco_sv.presentation.dto.response.PeakHourResponse;
import com.devplus.mikiosco_sv.presentation.dto.response.SalesReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetSalesReportUseCase {

    private final ReportQueryService queryService;

    public SalesReportResponse execute(LocalDate startDate, LocalDate endDate, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();
        OffsetDateTime from = startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime to   = endDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);

        // Summary
        Object[] summary = queryService.getSalesSummary(comedorId, from, to);
        BigDecimal totalRevenue = (BigDecimal) summary[0];
        long orderCount = ((Number) summary[1]).longValue();
        BigDecimal avgTicket = orderCount == 0
                ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP);

        // By category
        List<Object[]> catRows = queryService.getSalesByCategory(comedorId, from, to);
        List<CategorySalesResponse> byCategory = catRows.stream()
                .map(row -> {
                    BigDecimal catRevenue = (BigDecimal) row[1];
                    BigDecimal pct = totalRevenue.compareTo(BigDecimal.ZERO) == 0
                            ? BigDecimal.ZERO
                            : catRevenue.multiply(BigDecimal.valueOf(100))
                                    .divide(totalRevenue, 1, RoundingMode.HALF_UP);
                    return CategorySalesResponse.builder()
                            .categoryName((String) row[0])
                            .revenue(catRevenue)
                            .qtySold(((Number) row[2]).longValue())
                            .percentage(pct)
                            .build();
                })
                .toList();

        // Peak hours
        List<Object[]> hourRows = queryService.getPeakHours(comedorId, from, to);
        List<PeakHourResponse> peakHours = hourRows.stream()
                .map(row -> {
                    int hour = ((Number) row[0]).intValue();
                    return PeakHourResponse.builder()
                            .hour(hour)
                            .hourLabel(String.format("%02d:00 - %02d:00", hour, hour + 1))
                            .orderCount(((Number) row[1]).longValue())
                            .revenue((BigDecimal) row[2])
                            .build();
                })
                .toList();

        return SalesReportResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalRevenue(totalRevenue)
                .orderCount(orderCount)
                .avgTicket(avgTicket)
                .byCategory(byCategory)
                .peakHours(peakHours)
                .build();
    }
}
