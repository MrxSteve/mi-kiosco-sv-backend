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
public class AdminComedorDetailResponse {

    private final UUID id;
    private final String name;
    private final String legalName;
    private final String email;
    private final String phone;
    private final String address;
    private final String timezone;
    private final GenericStatus status;
    private final long userCount;
    private final OffsetDateTime createdAt;

    private final SubscriptionInfo subscription;

    @Getter
    @Builder
    public static class SubscriptionInfo {
        private final UUID suscripcionId;
        private final String planName;
        private final BigDecimal planPrice;
        private final SubscriptionStatus status;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final boolean autoRenew;
        private final String paymentReference;
    }
}
