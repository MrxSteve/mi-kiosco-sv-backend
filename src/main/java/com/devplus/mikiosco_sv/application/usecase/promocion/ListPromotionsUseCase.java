package com.devplus.mikiosco_sv.application.usecase.promocion;

import com.devplus.mikiosco_sv.infrastructure.persistence.repository.PromocionRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.PromotionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListPromotionsUseCase {

    private final PromocionRepository promocionRepository;
    private final PromotionAssembler assembler;

    @Transactional(readOnly = true)
    public List<PromotionResponse> execute(AuthenticatedUser caller) {
        return promocionRepository
                .findByComedorIdOrderByCreatedAtDesc(caller.comedorId())
                .stream()
                .map(assembler::toResponse)
                .toList();
    }
}
