package com.devplus.mikiosco_sv.application.usecase.promocion;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.PromocionRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.PromotionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TogglePromotionStatusUseCase {

    private final PromocionRepository promocionRepository;
    private final PromotionAssembler assembler;

    @Transactional
    public PromotionResponse execute(UUID id, AuthenticatedUser caller) {
        var promo = promocionRepository.findByIdAndComedorId(id, caller.comedorId())
                .orElseThrow(() -> NotFoundException.of("Promoción", id));

        GenericStatus newStatus = promo.getStatus() == GenericStatus.ACTIVE
                ? GenericStatus.INACTIVE
                : GenericStatus.ACTIVE;

        promo.setStatus(newStatus);
        return assembler.toResponse(promocionRepository.save(promo));
    }
}
