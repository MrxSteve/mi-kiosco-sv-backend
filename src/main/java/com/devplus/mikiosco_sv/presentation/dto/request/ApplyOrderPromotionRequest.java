package com.devplus.mikiosco_sv.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplyOrderPromotionRequest {

    @NotBlank(message = "El código de promoción es obligatorio")
    private String code;
}
