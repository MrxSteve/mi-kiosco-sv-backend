package com.devplus.mikiosco_sv.application.usecase.reporte;

import com.devplus.mikiosco_sv.infrastructure.persistence.report.ReportQueryService;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.TopProductResponse;
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
public class GetTopProductsUseCase {

    private final ReportQueryService queryService;

    public List<TopProductResponse> execute(LocalDate startDate, LocalDate endDate, int limit,
                                            AuthenticatedUser caller) {
        OffsetDateTime from = startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime to   = endDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);

        List<Object[]> rows = queryService.getTopProducts(caller.comedorId(), from, to, limit);

        // Calcular total para porcentaje
        BigDecimal totalRevenue = rows.stream()
                .map(r -> (BigDecimal) r[3])
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return rows.stream()
                .map(row -> {
                    BigDecimal revenue = (BigDecimal) row[3];
                    BigDecimal pct = totalRevenue.compareTo(BigDecimal.ZERO) == 0
                            ? BigDecimal.ZERO
                            : revenue.multiply(BigDecimal.valueOf(100))
                                    .divide(totalRevenue, 1, RoundingMode.HALF_UP);
                    return TopProductResponse.builder()
                            .productName((String) row[0])
                            .categoryName((String) row[1])
                            .qtySold(((Number) row[2]).longValue())
                            .revenue(revenue)
                            .percentage(pct)
                            .build();
                })
                .toList();
    }
}
