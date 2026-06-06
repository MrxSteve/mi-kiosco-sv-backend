package com.devplus.mikiosco_sv.presentation.dto.response;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class ComedorResponse {

    private final UUID id;
    private final String name;
    private final String legalName;
    private final String email;
    private final String phone;
    private final String address;
    private final String logoUrl;
    private final String timezone;
    private final GenericStatus status;
    private final OffsetDateTime createdAt;
}
