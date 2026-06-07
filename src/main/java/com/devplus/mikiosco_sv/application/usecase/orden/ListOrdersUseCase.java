package com.devplus.mikiosco_sv.application.usecase.orden;

import com.devplus.mikiosco_sv.domain.model.OrderStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.OrdenEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.OrdenRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListOrdersUseCase {

    private final OrdenRepository ordenRepository;
    private final OrderAssembler assembler;

    @Transactional(readOnly = true)
    public List<OrderResponse> execute(OrderStatus status, LocalDate date, Long orderNumber,
                                       AuthenticatedUser caller) {
        LocalDate targetDate = date != null ? date : LocalDate.now(ZoneOffset.UTC);
        OffsetDateTime from = targetDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime to   = from.plusDays(1);

        List<OrdenEntity> ordenes = ordenRepository
                .findByComedorIdAndDateRange(caller.comedorId(), from, to);

        // Filtros opcionales en Java (dataset pequeño por día)
        if (status != null) {
            ordenes = ordenes.stream().filter(o -> o.getStatus() == status).toList();
        }
        if (orderNumber != null) {
            ordenes = ordenes.stream().filter(o -> orderNumber.equals(o.getOrderNumber())).toList();
        }

        return assembler.assembleList(ordenes);
    }
}
