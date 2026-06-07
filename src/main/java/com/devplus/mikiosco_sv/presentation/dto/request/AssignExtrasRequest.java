package com.devplus.mikiosco_sv.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AssignExtrasRequest {

    @NotEmpty(message = "Debe incluir al menos un extra")
    private List<UUID> extraIds;
}
