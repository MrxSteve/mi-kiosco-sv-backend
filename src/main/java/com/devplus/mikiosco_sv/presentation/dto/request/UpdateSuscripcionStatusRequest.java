package com.devplus.mikiosco_sv.presentation.dto.request;

import com.devplus.mikiosco_sv.domain.model.SubscriptionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateSuscripcionStatusRequest {

    @NotNull(message = "El nuevo estado es obligatorio")
    private SubscriptionStatus newStatus;

    private String notes;
}
