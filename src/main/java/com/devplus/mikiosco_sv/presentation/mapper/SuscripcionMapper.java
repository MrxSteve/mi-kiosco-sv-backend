package com.devplus.mikiosco_sv.presentation.mapper;

import com.devplus.mikiosco_sv.infrastructure.persistence.entity.SuscripcionEntity;
import com.devplus.mikiosco_sv.presentation.dto.response.SubscriptionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface SuscripcionMapper {

    @Mapping(source = "plan.name",         target = "planName")
    @Mapping(source = "plan.price",        target = "planPrice")
    @Mapping(source = "plan.billingCycle", target = "billingCycle")
    SubscriptionResponse toResponse(SuscripcionEntity entity);
}
