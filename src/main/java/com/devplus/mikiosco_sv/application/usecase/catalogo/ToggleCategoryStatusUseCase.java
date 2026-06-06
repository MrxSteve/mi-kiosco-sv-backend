package com.devplus.mikiosco_sv.application.usecase.catalogo;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.CategoriaRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.CategoryResponse;
import com.devplus.mikiosco_sv.presentation.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ToggleCategoryStatusUseCase {

    private final CategoriaRepository categoriaRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponse execute(UUID id, AuthenticatedUser caller) {
        var categoria = categoriaRepository.findByIdAndComedorId(id, caller.comedorId())
                .orElseThrow(() -> NotFoundException.of("Categoría", id));

        GenericStatus newStatus = categoria.getStatus() == GenericStatus.ACTIVE
                ? GenericStatus.INACTIVE
                : GenericStatus.ACTIVE;

        categoria.setStatus(newStatus);
        return categoryMapper.toResponse(categoriaRepository.save(categoria));
    }
}
