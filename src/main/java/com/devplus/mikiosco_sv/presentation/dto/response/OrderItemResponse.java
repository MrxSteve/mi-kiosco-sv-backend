package com.devplus.mikiosco_sv.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class OrderItemResponse {

    private final UUID id;
    private final UUID productId;
    private final String productName;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final ComboInfo combo;
    private final List<ExtraInfo> extras;
    private final BigDecimal lineSubtotal;
    private final String notes;

    @Getter
    @Builder
    public static class ComboInfo {
        private final UUID comboId;
        private final String label;
        private final int comboQty;
        private final BigDecimal comboPrice;
    }

    @Getter
    @Builder
    public static class ExtraInfo {
        private final UUID extraId;
        private final String name;
        private final BigDecimal price;
    }
}
