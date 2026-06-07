package com.devplus.mikiosco_sv.application.usecase.superadmin;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.domain.model.SubscriptionStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.PlanSuscripcionRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.SuscripcionRepository;
import com.devplus.mikiosco_sv.presentation.dto.response.AdminPlanResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TogglePlanStatusUseCase {

    private final PlanSuscripcionRepository planRepository;
    private final SuscripcionRepository suscripcionRepository;

    @Transactional
    public AdminPlanResponse execute(UUID id) {
        var plan = planRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Plan", id));

        plan.setStatus(plan.getStatus() == GenericStatus.ACTIVE
                ? GenericStatus.INACTIVE : GenericStatus.ACTIVE);

        var saved = planRepository.save(plan);
        long active = suscripcionRepository.countByPlanIdAndStatus(id, SubscriptionStatus.ACTIVE);
        long total  = suscripcionRepository.findByPlanId(id).size();

        return AdminPlanResponse.builder()
                .id(saved.getId())
                .code(saved.getCode())
                .name(saved.getName())
                .description(saved.getDescription())
                .price(saved.getPrice())
                .billingCycle(saved.getBillingCycle())
                .userLimit(saved.getUserLimit())
                .status(saved.getStatus())
                .activeSubscribers(active)
                .totalSubscribers(total)
                .monthlyRevenue(saved.getPrice().multiply(BigDecimal.valueOf(active)))
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }
}
