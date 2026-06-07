package com.devplus.mikiosco_sv.application.usecase.cocina;

import com.devplus.mikiosco_sv.domain.exception.BadRequestException;
import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.domain.model.OrderStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.HistorialEstadoOrdenEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.HistorialEstadoOrdenRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.OrdenRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.UpdateOrderStatusRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.KitchenOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateKitchenOrderStatusUseCase {

    private final OrdenRepository ordenRepository;
    private final HistorialEstadoOrdenRepository historialRepository;
    private final KitchenOrderAssembler assembler;

    @Transactional
    public KitchenOrderResponse execute(UUID id, UpdateOrderStatusRequest request, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        var orden = ordenRepository.findByIdAndComedorId(id, comedorId)
                .orElseThrow(() -> NotFoundException.of("Orden", id));

        if (orden.getSentToKitchenAt() == null) {
            throw new BadRequestException("La orden aún no ha sido enviada a cocina");
        }

        validateTransition(orden.getStatus(), request.getNewStatus());

        var now = OffsetDateTime.now();
        var previousStatus = orden.getStatus();

        orden.setStatus(request.getNewStatus());

        if (request.getNewStatus() == OrderStatus.COMPLETED) {
            orden.setCompletedAt(now);
        }

        historialRepository.save(HistorialEstadoOrdenEntity.builder()
                .comedorId(comedorId)
                .ordenId(id)
                .previousStatus(previousStatus)
                .newStatus(request.getNewStatus())
                .changedByUserId(caller.userId())
                .changedAt(now)
                .build());

        return assembler.assembleSingle(ordenRepository.save(orden));
    }

    private void validateTransition(OrderStatus current, OrderStatus requested) {
        boolean valid = switch (current) {
            case PENDING         -> requested == OrderStatus.IN_PREPARATION;
            case IN_PREPARATION  -> requested == OrderStatus.COMPLETED || requested == OrderStatus.PENDING;
            default              -> false;
        };
        if (!valid) {
            throw new BadRequestException(
                    "Transición de estado inválida: " + current + " → " + requested);
        }
    }
}
