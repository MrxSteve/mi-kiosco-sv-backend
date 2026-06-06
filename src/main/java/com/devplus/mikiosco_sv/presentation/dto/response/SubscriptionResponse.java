package com.devplus.mikiosco_sv.presentation.dto.response;

import com.devplus.mikiosco_sv.domain.model.SubscriptionStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class SubscriptionResponse {

    private final UUID id;
    private final String planName;
    private final BigDecimal planPrice;
    private final String billingCycle;
    private final SubscriptionStatus status;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final boolean autoRenew;
    private final String paymentReference;
    private final OffsetDateTime createdAt;
}
