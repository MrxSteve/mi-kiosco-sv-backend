package com.devplus.mikiosco_sv.presentation.dto.response;

import com.devplus.mikiosco_sv.domain.model.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class KitchenOrderResponse {

    private final UUID id;
    private final Long orderNumber;
    private final OrderStatus status;
    private final String customerDisplayName;
    private final String notes;
    private final List<OrderItemResponse> items;
    private final OffsetDateTime sentToKitchenAt;
    private final long minutesSinceArrival;
    private final OffsetDateTime createdAt;
}
