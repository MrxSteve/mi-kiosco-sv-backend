package com.devplus.mikiosco_sv.infrastructure.security;

import com.devplus.mikiosco_sv.domain.model.UserRole;

import java.util.UUID;

public record AuthenticatedUser(
        UUID userId,
        UUID comedorId,   // null para super_admin
        UserRole role,
        String email
) {
    public boolean isSuperAdmin() {
        return role == UserRole.SUPER_ADMIN;
    }
}
