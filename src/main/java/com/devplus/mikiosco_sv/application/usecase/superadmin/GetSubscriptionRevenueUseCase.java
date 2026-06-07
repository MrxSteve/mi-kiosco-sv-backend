package com.devplus.mikiosco_sv.application.usecase.superadmin;

import com.devplus.mikiosco_sv.domain.model.SubscriptionStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.PlanSuscripcionRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.SuscripcionRepository;
import com.devplus.mikiosco_sv.presentation.dto.response.SubscriptionRevenueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetSubscriptionRevenueUseCase {

    private final PlanSuscripcionRepository planRepository;
    private final SuscripcionRepository suscripcionRepository;

    @Transactional(readOnly = true)
    public SubscriptionRevenueResponse execute() {
        long active    = suscripcionRepository.countByStatus(SubscriptionStatus.ACTIVE);
        long expired   = suscripcionRepository.countByStatus(SubscriptionStatus.EXPIRED);
        long suspended = suscripcionRepository.countByStatus(SubscriptionStatus.SUSPENDED);
        long cancelled = suscripcionRepository.countByStatus(SubscriptionStatus.CANCELLED);
        long total     = suscripcionRepository.count();

        // MRR por plan
        List<SubscriptionRevenueResponse.PlanRevenue> byPlan = planRepository.findAll().stream()
                .map(plan -> {
                    long activeCount = suscripcionRepository
                            .countByPlanIdAndStatus(plan.getId(), SubscriptionStatus.ACTIVE);
                    BigDecimal monthlyRev = plan.getPrice()
                            .multiply(BigDecimal.valueOf(activeCount));
                    return SubscriptionRevenueResponse.PlanRevenue.builder()
                            .planName(plan.getName())
                            .planPrice(plan.getPrice())
                            .activeSubscribers(activeCount)
                            .monthlyRevenue(monthlyRev)
                            .build();
                })
                .toList();

        BigDecimal mrr = byPlan.stream()
                .map(SubscriptionRevenueResponse.PlanRevenue::getMonthlyRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return SubscriptionRevenueResponse.builder()
                .activeSubscriptions(active)
                .expiredSubscriptions(expired)
                .suspendedSubscriptions(suspended)
                .cancelledSubscriptions(cancelled)
                .totalSubscriptionsAllTime(total)
                .mrr(mrr)
                .arr(mrr.multiply(BigDecimal.valueOf(12)))
                .byPlan(byPlan)
                .build();
    }
}
