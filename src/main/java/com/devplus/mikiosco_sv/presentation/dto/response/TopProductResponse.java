package com.devplus.mikiosco_sv.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TopProductResponse {

    private final String productName;
    private final String categoryName;
    private final long qtySold;
    private final BigDecimal revenue;
    private final BigDecimal percentage;
}
