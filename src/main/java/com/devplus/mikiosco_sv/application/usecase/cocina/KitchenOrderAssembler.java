package com.devplus.mikiosco_sv.application.usecase.cocina;

import com.devplus.mikiosco_sv.application.usecase.orden.OrderAssembler;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.OrdenEntity;
import com.devplus.mikiosco_sv.presentation.dto.response.KitchenOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class KitchenOrderAssembler {

    private final OrderAssembler orderAssembler;

    public KitchenOrderResponse assembleSingle(OrdenEntity orden) {
        return assembleList(List.of(orden)).get(0);
    }

    public List<KitchenOrderResponse> assembleList(List<OrdenEntity> ordenes) {
        if (ordenes.isEmpty()) return List.of();

        // Reutiliza el assembler del módulo 6 para obtener items y cliente
        var orderResponses = orderAssembler.assembleList(ordenes);

        return orderResponses.stream()
                .map(or -> {
                    long minutes = 0;
                    if (or.getSentToKitchenAt() != null) {
                        minutes = Duration.between(or.getSentToKitchenAt(), OffsetDateTime.now())
                                .toMinutes();
                    }
                    return KitchenOrderResponse.builder()
                            .id(or.getId())
                            .orderNumber(or.getOrderNumber())
                            .status(or.getStatus())
                            .customerDisplayName(or.getCustomerDisplayName())
                            .notes(or.getNotes())
                            .items(or.getItems())
                            .sentToKitchenAt(or.getSentToKitchenAt())
                            .minutesSinceArrival(minutes)
                            .createdAt(or.getCreatedAt())
                            .build();
                })
                .toList();
    }
}
