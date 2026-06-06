package com.devplus.mikiosco_sv.presentation.dto.response;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.domain.model.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class UserResponse {

    private final UUID id;
    private final String fullName;
    private final String email;
    private final UserRole role;
    private final GenericStatus status;
    private final UUID comedorId;
    private final OffsetDateTime lockedUntil;
    private final OffsetDateTime lastLoginAt;
    private final OffsetDateTime createdAt;
}
