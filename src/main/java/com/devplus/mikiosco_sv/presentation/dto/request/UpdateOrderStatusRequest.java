package com.devplus.mikiosco_sv.presentation.dto.request;

import com.devplus.mikiosco_sv.domain.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {

    @NotNull(message = "El nuevo estado es obligatorio")
    private OrderStatus newStatus;
}
