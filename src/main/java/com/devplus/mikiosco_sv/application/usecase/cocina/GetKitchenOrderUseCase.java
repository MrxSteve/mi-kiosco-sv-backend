package com.devplus.mikiosco_sv.application.usecase.cocina;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.OrdenRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.KitchenOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetKitchenOrderUseCase {

    private final OrdenRepository ordenRepository;
    private final KitchenOrderAssembler assembler;

    @Transactional(readOnly = true)
    public KitchenOrderResponse execute(UUID id, AuthenticatedUser caller) {
        var orden = ordenRepository.findByIdAndComedorId(id, caller.comedorId())
                .orElseThrow(() -> NotFoundException.of("Orden", id));
        return assembler.assembleSingle(orden);
    }
}
