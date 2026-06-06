package com.devplus.mikiosco_sv.presentation.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateComedorRequest {

    @Size(min = 2, max = 150)
    private String name;

    @Size(max = 200)
    private String legalName;

    @Size(max = 30)
    private String phone;

    @Size(max = 255)
    private String address;

    private String logoUrl;

    @Size(max = 80)
    private String timezone;
}
