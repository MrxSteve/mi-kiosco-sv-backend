package com.devplus.mikiosco_sv.presentation.dto.response;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.domain.model.SubscriptionStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class AdminSuscripcionResponse {

    private final UUID id;
    private final UUID comedorId;
    private final String comedorName;
    private final String comedorEmail;
    private final GenericStatus comedorStatus;
    private final String planCode;
    private final String planName;
    private final BigDecimal planPrice;
    private final SubscriptionStatus status;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final boolean autoRenew;
    private final String paymentReference;
    private final String notes;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
}
