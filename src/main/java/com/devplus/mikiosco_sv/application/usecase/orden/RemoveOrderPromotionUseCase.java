package com.devplus.mikiosco_sv.application.usecase.orden;

import com.devplus.mikiosco_sv.domain.exception.BadRequestException;
import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
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
public class RemoveOrderPromotionUseCase {

    private final OrdenRepository ordenRepository;
    private final OrderAssembler assembler;

    @Transactional
    public OrderResponse execute(UUID ordenId, AuthenticatedUser caller) {
        var orden = ordenRepository.findByIdAndComedorId(ordenId, caller.comedorId())
                .orElseThrow(() -> NotFoundException.of("Orden", ordenId));

        if (orden.getSentToKitchenAt() != null) {
            throw new BadRequestException("La orden ya fue enviada a cocina");
        }
        if (orden.getPromocionId() == null) {
            throw new BadRequestException("La orden no tiene una promoción aplicada");
        }

        orden.setPromocionId(null);
        orden.setDiscountAmount(BigDecimal.ZERO);
        orden.setTotalAmount(orden.getSubtotal());

        return assembler.assembleSingle(ordenRepository.save(orden));
    }
}
