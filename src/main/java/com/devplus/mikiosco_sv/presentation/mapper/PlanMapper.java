package com.devplus.mikiosco_sv.presentation.mapper;

import com.devplus.mikiosco_sv.infrastructure.persistence.entity.PlanSuscripcionEntity;
import com.devplus.mikiosco_sv.presentation.dto.response.PlanResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface PlanMapper {

    PlanResponse toResponse(PlanSuscripcionEntity entity);

    List<PlanResponse> toResponseList(List<PlanSuscripcionEntity> entities);
}
