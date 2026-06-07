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
public class DeliverOrderUseCase {

    private final OrdenRepository ordenRepository;
    private final HistorialEstadoOrdenRepository historialRepository;
    private final OrderAssembler assembler;

    @Transactional
    public OrderResponse execute(UUID ordenId, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        var orden = ordenRepository.findByIdAndComedorId(ordenId, comedorId)
                .orElseThrow(() -> NotFoundException.of("Orden", ordenId));

        if (orden.getStatus() != OrderStatus.COMPLETED) {
            throw new BadRequestException(
                    "Solo se pueden entregar órdenes con estado COMPLETED. Estado actual: "
                    + orden.getStatus());
        }

        var now = OffsetDateTime.now();
        orden.setStatus(OrderStatus.DELIVERED);
        orden.setDeliveredAt(now);

        historialRepository.save(HistorialEstadoOrdenEntity.builder()
                .comedorId(comedorId)
                .ordenId(ordenId)
                .previousStatus(OrderStatus.COMPLETED)
                .newStatus(OrderStatus.DELIVERED)
                .changedByUserId(caller.userId())
                .changeNotes("Orden entregada al cliente")
                .changedAt(now)
                .build());

        return assembler.assembleSingle(ordenRepository.save(orden));
    }
}
