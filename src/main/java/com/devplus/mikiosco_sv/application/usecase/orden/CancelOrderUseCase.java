package com.devplus.mikiosco_sv.application.usecase.orden;

import com.devplus.mikiosco_sv.domain.exception.BadRequestException;
import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.domain.model.OrderStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.HistorialEstadoOrdenEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.HistorialEstadoOrdenRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.OrdenRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CancelOrderUseCase {

    private final OrdenRepository ordenRepository;
    private final HistorialEstadoOrdenRepository historialRepository;
    private final OrderAssembler assembler;

    @Transactional
    public OrderResponse execute(UUID ordenId, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        var orden = ordenRepository.findByIdAndComedorId(ordenId, comedorId)
                .orElseThrow(() -> NotFoundException.of("Orden", ordenId));

        if (orden.getSentToKitchenAt() != null) {
            throw new BadRequestException("La orden ya fue enviada a cocina y no puede cancelarse");
        }
        if (orden.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("La orden ya está cancelada");
        }

        var now = OffsetDateTime.now();
        var previousStatus = orden.getStatus();

        orden.setStatus(OrderStatus.CANCELLED);
        orden.setCancelledAt(now);

        historialRepository.save(HistorialEstadoOrdenEntity.builder()
                .comedorId(comedorId)
                .ordenId(ordenId)
                .previousStatus(previousStatus)
                .newStatus(OrderStatus.CANCELLED)
                .changedByUserId(caller.userId())
                .changeNotes("Orden cancelada")
                .changedAt(now)
                .build());

        return assembler.assembleSingle(ordenRepository.save(orden));
    }
}
