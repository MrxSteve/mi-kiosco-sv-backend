package com.devplus.mikiosco_sv.presentation.dto.response;

import com.devplus.mikiosco_sv.domain.model.DiscountType;
import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class PromotionResponse {

    private final UUID id;
    private final String code;
    private final String name;
    private final DiscountType discountKind;
    private final BigDecimal discountValue;
    private final OffsetDateTime startsAt;
    private final OffsetDateTime endsAt;
    private final Integer maxUses;
    private final int usesCount;
    private final boolean appliesToAll;
    private final List<UUID> productIds;
    private final GenericStatus status;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
}
