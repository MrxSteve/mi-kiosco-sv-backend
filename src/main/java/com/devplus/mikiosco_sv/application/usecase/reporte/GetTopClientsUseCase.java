package com.devplus.mikiosco_sv.application.usecase.reporte;

import com.devplus.mikiosco_sv.infrastructure.persistence.report.ReportQueryService;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.TopClientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetTopClientsUseCase {

    private final ReportQueryService queryService;

    public List<TopClientResponse> execute(int limit, AuthenticatedUser caller) {
        return queryService.getTopClients(caller.comedorId(), limit)
                .stream()
                .map(row -> TopClientResponse.builder()
                        .clientName((String) row[0])
                        .customerCode((String) row[1])
                        .orderCount(((Number) row[2]).longValue())
                        .totalSpent((BigDecimal) row[3])
                        .lastOrderAt(row[4] == null ? null : ((java.sql.Timestamp) row[4]).toInstant()
                                .atOffset(java.time.ZoneOffset.UTC))
                        .build())
                .toList();
    }
}
