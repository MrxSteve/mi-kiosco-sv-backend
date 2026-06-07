package com.devplus.mikiosco_sv.presentation.dto.response;

import com.devplus.mikiosco_sv.domain.model.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Builder
public class ClientOrderHistoryResponse {

    private final Long orderNumber;
    private final OrderStatus status;
    private final BigDecimal total;
    private final OffsetDateTime createdAt;
}
