package com.devplus.mikiosco_sv.application.usecase.catalogo;

import com.devplus.mikiosco_sv.domain.exception.ConflictException;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.CategoriaEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.CategoriaRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.CreateCategoryRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.CategoryResponse;
import com.devplus.mikiosco_sv.presentation.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateCategoryUseCase {

    private final CategoriaRepository categoriaRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponse execute(CreateCategoryRequest request, AuthenticatedUser caller) {
        if (categoriaRepository.existsByComedorIdAndNameIgnoreCase(caller.comedorId(), request.getName())) {
            throw new ConflictException("Ya existe una categoría con ese nombre");
        }

        var entity = CategoriaEntity.builder()
                .comedorId(caller.comedorId())
                .name(request.getName())
                .description(request.getDescription())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();

        return categoryMapper.toResponse(categoriaRepository.save(entity));
    }
}
