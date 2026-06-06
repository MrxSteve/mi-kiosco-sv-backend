package com.devplus.mikiosco_sv.presentation.dto.response;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class CategoryResponse {

    private final UUID id;
    private final String name;
    private final String description;
    private final Integer displayOrder;
    private final GenericStatus status;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
}
