package com.devplus.mikiosco_sv.presentation.mapper;

import com.devplus.mikiosco_sv.infrastructure.persistence.entity.UsuarioEntity;
import com.devplus.mikiosco_sv.presentation.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface UserMapper {

    @Mapping(source = "comedor.id", target = "comedorId")
    UserResponse toResponse(UsuarioEntity entity);

    List<UserResponse> toResponseList(List<UsuarioEntity> entities);
}
