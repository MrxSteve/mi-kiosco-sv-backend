package com.devplus.mikiosco_sv.presentation.mapper;

import com.devplus.mikiosco_sv.infrastructure.persistence.entity.CategoriaEntity;
import com.devplus.mikiosco_sv.presentation.dto.response.CategoryResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    CategoryResponse toResponse(CategoriaEntity entity);

    List<CategoryResponse> toResponseList(List<CategoriaEntity> entities);
}
