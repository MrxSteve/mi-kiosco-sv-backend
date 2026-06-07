package com.devplus.mikiosco_sv.application.usecase.orden;

import com.devplus.mikiosco_sv.domain.exception.BadRequestException;
import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.OrdenDetalleEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.OrdenDetalleRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.OrdenRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RemoveOrderItemUseCase {

    private final OrdenRepository ordenRepository;
    private final OrdenDetalleRepository detalleRepository;
    private final OrderAssembler assembler;

    @Transactional
    public OrderResponse execute(UUID ordenId, UUID itemId, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        var orden = ordenRepository.findByIdAndComedorId(ordenId, comedorId)
                .orElseThrow(() -> NotFoundException.of("Orden", ordenId));

        if (orden.getSentToKitchenAt() != null) {
            throw new BadRequestException("La orden ya fue enviada a cocina y no puede modificarse");
        }

        var detalle = detalleRepository.findByIdAndOrdenIdAndComedorId(itemId, ordenId, comedorId)
                .orElseThrow(() -> NotFoundException.of("Item de orden", itemId));

        detalleRepository.delete(detalle);

        // Recalcular totales
        BigDecimal subtotal = detalleRepository.findByOrdenIdOrderByCreatedAtAsc(ordenId)
                .stream()
                .map(OrdenDetalleEntity::getLineSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        orden.setSubtotal(subtotal);
        orden.setTotalAmount(subtotal.subtract(orden.getDiscountAmount()));

        return assembler.assembleSingle(ordenRepository.save(orden));
    }
}
