package com.devplus.mikiosco_sv.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminStatsResponse {

    private final long totalComedores;
    private final long activeComedores;
    private final long inactiveComedores;
    private final long totalUsers;
}
