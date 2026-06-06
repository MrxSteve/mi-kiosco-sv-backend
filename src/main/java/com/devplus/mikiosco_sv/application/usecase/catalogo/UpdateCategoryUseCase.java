package com.devplus.mikiosco_sv.application.usecase.catalogo;

import com.devplus.mikiosco_sv.domain.exception.ConflictException;
import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.CategoriaRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.UpdateCategoryRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.CategoryResponse;
import com.devplus.mikiosco_sv.presentation.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateCategoryUseCase {

    private final CategoriaRepository categoriaRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponse execute(UUID id, UpdateCategoryRequest request, AuthenticatedUser caller) {
        var categoria = categoriaRepository.findByIdAndComedorId(id, caller.comedorId())
                .orElseThrow(() -> NotFoundException.of("Categoría", id));

        if (request.getName() != null) {
            if (categoriaRepository.existsByComedorIdAndNameIgnoreCaseAndIdNot(
                    caller.comedorId(), request.getName(), id)) {
                throw new ConflictException("Ya existe una categoría con ese nombre");
            }
            categoria.setName(request.getName());
        }
        if (request.getDescription() != null) categoria.setDescription(request.getDescription());
        if (request.getDisplayOrder() != null) categoria.setDisplayOrder(request.getDisplayOrder());

        return categoryMapper.toResponse(categoriaRepository.save(categoria));
    }
}
