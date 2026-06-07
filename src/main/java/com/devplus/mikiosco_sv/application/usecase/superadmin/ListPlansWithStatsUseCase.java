package com.devplus.mikiosco_sv.application.usecase.superadmin;

import com.devplus.mikiosco_sv.domain.model.SubscriptionStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.PlanSuscripcionRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.SuscripcionRepository;
import com.devplus.mikiosco_sv.presentation.dto.response.AdminPlanResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListPlansWithStatsUseCase {

    private final PlanSuscripcionRepository planRepository;
    private final SuscripcionRepository suscripcionRepository;

    @Transactional(readOnly = true)
    public List<AdminPlanResponse> execute() {
        return planRepository.findAll().stream()
                .map(plan -> {
                    long active = suscripcionRepository.countByPlanIdAndStatus(
                            plan.getId(), SubscriptionStatus.ACTIVE);
                    long total  = suscripcionRepository.findByPlanId(plan.getId()).size();
                    return AdminPlanResponse.builder()
                            .id(plan.getId())
                            .code(plan.getCode())
                            .name(plan.getName())
                            .description(plan.getDescription())
                            .price(plan.getPrice())
                            .billingCycle(plan.getBillingCycle())
                            .userLimit(plan.getUserLimit())
                            .status(plan.getStatus())
                            .activeSubscribers(active)
                            .totalSubscribers(total)
                            .monthlyRevenue(plan.getPrice().multiply(
                                    java.math.BigDecimal.valueOf(active)))
                            .createdAt(plan.getCreatedAt())
                            .updatedAt(plan.getUpdatedAt())
                            .build();
                })
                .toList();
    }
}
