package com.devplus.mikiosco_sv.presentation.dto.response;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ProductResponse {

    private final UUID id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final String imageUrl;
    private final UUID categoryId;
    private final String categoryName;
    private final boolean hasExtras;
    private final boolean hasCombos;
    private final GenericStatus status;
    private final List<ComboResponse> combos;
    private final List<ProductExtraGroupResponse> extraGroups;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
}
