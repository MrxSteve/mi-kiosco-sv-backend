package com.devplus.mikiosco_sv.presentation.dto.response;

import com.devplus.mikiosco_sv.domain.model.AuditAction;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class AuditLogResponse {

    private final UUID id;
    private final AuditAction action;
    private final String entityName;
    private final UUID entityId;
    private final String detail;
    private final UUID userId;
    private final OffsetDateTime createdAt;
}
