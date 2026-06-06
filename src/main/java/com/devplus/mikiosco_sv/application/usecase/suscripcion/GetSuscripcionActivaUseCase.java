package com.devplus.mikiosco_sv.application.usecase.suscripcion;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.domain.model.SubscriptionStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.SuscripcionRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.SubscriptionResponse;
import com.devplus.mikiosco_sv.presentation.mapper.SuscripcionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetSuscripcionActivaUseCase {

    private final SuscripcionRepository suscripcionRepository;
    private final SuscripcionMapper suscripcionMapper;

    @Transactional(readOnly = true)
    public SubscriptionResponse execute(AuthenticatedUser caller) {
        var suscripcion = suscripcionRepository
                .findByComedor_IdAndStatus(caller.comedorId(), SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(
                        "No hay suscripción activa para este comedor"));
        return suscripcionMapper.toResponse(suscripcion);
    }
}
