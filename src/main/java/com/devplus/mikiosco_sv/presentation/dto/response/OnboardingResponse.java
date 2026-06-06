package com.devplus.mikiosco_sv.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class OnboardingResponse {

    private final UUID comedorId;
    private final UUID adminUserId;
    private final String paymentReference;
    private final String message;
}
