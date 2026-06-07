package com.devplus.mikiosco_sv.presentation.dto.response;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class ClientDetailResponse {

    private final UUID id;
    private final String fullName;
    private final String email;
    private final String phone;
    private final String address;
    private final String customerCode;
    private final String notes;
    private final GenericStatus status;
    private final long orderCount;
    private final BigDecimal totalSpent;
    private final OffsetDateTime lastOrderAt;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
}
