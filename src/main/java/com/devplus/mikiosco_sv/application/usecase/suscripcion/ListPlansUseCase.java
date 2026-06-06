package com.devplus.mikiosco_sv.application.usecase.suscripcion;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.PlanSuscripcionRepository;
import com.devplus.mikiosco_sv.presentation.dto.response.PlanResponse;
import com.devplus.mikiosco_sv.presentation.mapper.PlanMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListPlansUseCase {

    private final PlanSuscripcionRepository planRepository;
    private final PlanMapper planMapper;

    @Transactional(readOnly = true)
    public List<PlanResponse> execute() {
        return planMapper.toResponseList(
                planRepository.findAllByStatus(GenericStatus.ACTIVE)
        );
    }
}
