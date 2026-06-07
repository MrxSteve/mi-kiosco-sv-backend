package com.devplus.mikiosco_sv.presentation.dto.response;

import com.devplus.mikiosco_sv.domain.model.OrderStatus;
import com.devplus.mikiosco_sv.domain.model.PaymentMethod;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class OrderResponse {

    private final UUID id;
    private final Long orderNumber;
    private final OrderStatus status;
    private final OffsetDateTime sentToKitchenAt;

    // Identificación del cliente
    private final UUID clienteId;
    private final String customerDisplayName;
    private final String customerCode;

    private final String notes;
    private final List<OrderItemResponse> items;

    private final BigDecimal subtotal;
    private final BigDecimal discountAmount;
    private final String promotionCode;
    private final BigDecimal total;

    private final PaymentInfo payment;

    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    @Getter
    @Builder
    public static class PaymentInfo {
        private final BigDecimal amountReceived;
        private final BigDecimal changeAmount;
        private final PaymentMethod method;
        private final OffsetDateTime paidAt;
    }
}
