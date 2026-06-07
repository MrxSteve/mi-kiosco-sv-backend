package com.devplus.mikiosco_sv.presentation.dto.response;

import com.devplus.mikiosco_sv.domain.model.PaymentMethod;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class RegisterPaymentResponse {

    private final UUID orderId;
    private final Long orderNumber;
    private final BigDecimal amountReceived;
    private final BigDecimal changeAmount;
    private final PaymentMethod method;
    private final OffsetDateTime paidAt;
}
