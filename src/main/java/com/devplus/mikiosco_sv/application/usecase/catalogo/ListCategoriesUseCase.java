package com.devplus.mikiosco_sv.application.usecase.catalogo;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.CategoriaRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.CategoryResponse;
import com.devplus.mikiosco_sv.presentation.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListCategoriesUseCase {

    private final CategoriaRepository categoriaRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<CategoryResponse> execute(boolean onlyActive, AuthenticatedUser caller) {
        var categories = onlyActive
                ? categoriaRepository.findByComedorIdAndStatusOrderByDisplayOrderAscNameAsc(
                        caller.comedorId(), GenericStatus.ACTIVE)
                : categoriaRepository.findByComedorIdOrderByDisplayOrderAscNameAsc(caller.comedorId());

        return categoryMapper.toResponseList(categories);
    }
}
