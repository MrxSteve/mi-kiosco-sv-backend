package com.devplus.mikiosco_sv.presentation.mapper;

import com.devplus.mikiosco_sv.infrastructure.persistence.entity.ComedorEntity;
import com.devplus.mikiosco_sv.presentation.dto.response.ComedorResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ComedorMapper {

    ComedorResponse toResponse(ComedorEntity entity);

    List<ComedorResponse> toResponseList(List<ComedorEntity> entities);
}
