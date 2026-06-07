package com.devplus.mikiosco_sv.presentation.dto.response;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class AdminPlanResponse {

    private final UUID id;
    private final String code;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final String billingCycle;
    private final int userLimit;
    private final GenericStatus status;
    private final long activeSubscribers;
    private final long totalSubscribers;
    private final BigDecimal monthlyRevenue;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
}
