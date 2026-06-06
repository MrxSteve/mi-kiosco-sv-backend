package com.devplus.mikiosco_sv.presentation.mapper;

import com.devplus.mikiosco_sv.infrastructure.persistence.entity.ConfiguracionComedorEntity;
import com.devplus.mikiosco_sv.presentation.dto.response.ConfiguracionComedorResponse;
import org.mapstruct.Mapper;

@Mapper
public interface ConfiguracionComedorMapper {

    ConfiguracionComedorResponse toResponse(ConfiguracionComedorEntity entity);
}
