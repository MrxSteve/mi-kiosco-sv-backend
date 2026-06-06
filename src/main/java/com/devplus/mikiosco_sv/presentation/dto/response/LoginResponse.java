package com.devplus.mikiosco_sv.presentation.dto.response;

import com.devplus.mikiosco_sv.domain.model.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class LoginResponse {

    private final String token;
    private final long expiresIn;
    private final UserInfo user;

    @Getter
    @Builder
    public static class UserInfo {
        private final UUID id;
        private final String fullName;
        private final String email;
        private final UserRole role;
        private final UUID comedorId;
    }
}
