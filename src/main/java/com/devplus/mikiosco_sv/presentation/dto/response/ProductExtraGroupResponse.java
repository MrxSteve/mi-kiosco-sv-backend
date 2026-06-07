package com.devplus.mikiosco_sv.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ProductExtraGroupResponse {

    private final UUID groupId;
    private final String groupName;
    private final List<Item> extras;

    @Getter
    @Builder
    public static class Item {
        private final UUID id;
        private final String name;
        private final BigDecimal price;
    }
}
