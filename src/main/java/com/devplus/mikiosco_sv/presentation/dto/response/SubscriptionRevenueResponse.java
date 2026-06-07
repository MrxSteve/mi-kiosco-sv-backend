package com.devplus.mikiosco_sv.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class SubscriptionRevenueResponse {

    private final long activeSubscriptions;
    private final long expiredSubscriptions;
    private final long suspendedSubscriptions;
    private final long cancelledSubscriptions;
    private final long totalSubscriptionsAllTime;

    /** Monthly Recurring Revenue — suma de plan.price de todas las suscripciones activas. */
    private final BigDecimal mrr;

    /** Annual Recurring Revenue — MRR * 12. */
    private final BigDecimal arr;

    private final List<PlanRevenue> byPlan;

    @Getter
    @Builder
    public static class PlanRevenue {
        private final String planName;
        private final BigDecimal planPrice;
        private final long activeSubscribers;
        private final BigDecimal monthlyRevenue;
    }
}
