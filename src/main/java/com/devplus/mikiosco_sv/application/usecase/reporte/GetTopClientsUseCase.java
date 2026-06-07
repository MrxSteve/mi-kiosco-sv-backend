package com.devplus.mikiosco_sv.application.usecase.reporte;

import com.devplus.mikiosco_sv.infrastructure.persistence.report.ReportQueryService;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.TopClientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
                        .lastOrderAt(toOffsetDateTime(row[4]))
                        .build())
                .toList();
    }

    // Hibernate 6 + PostgreSQL JDBC puede devolver TIMESTAMPTZ como Instant, OffsetDateTime
    // o java.sql.Timestamp según el driver y la versión. Manejamos los tres casos.
    private OffsetDateTime toOffsetDateTime(Object value) {
        if (value == null) return null;
        if (value instanceof OffsetDateTime odt)    return odt;
        if (value instanceof Instant instant)       return instant.atOffset(ZoneOffset.UTC);
        if (value instanceof java.sql.Timestamp ts) return ts.toInstant().atOffset(ZoneOffset.UTC);
        return null;
    }
}
