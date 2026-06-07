package com.devplus.mikiosco_sv.application.usecase.catalogo;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ProductoRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListProductsUseCase {

    private final ProductoRepository productoRepository;
    private final ProductAssembler assembler;

    @Transactional(readOnly = true)
    public List<ProductResponse> execute(UUID categoryId, boolean onlyActive, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        var productos = categoryId != null
                ? (onlyActive
                        ? productoRepository.findByComedorIdAndCategoriaIdAndStatusOrderByNameAsc(
                                comedorId, categoryId, GenericStatus.ACTIVE)
                        : productoRepository.findByComedorIdAndCategoriaIdOrderByNameAsc(comedorId, categoryId))
                : (onlyActive
                        ? productoRepository.findByComedorIdAndStatusOrderByNameAsc(comedorId, GenericStatus.ACTIVE)
                        : productoRepository.findByComedorIdOrderByNameAsc(comedorId));

        return assembler.assembleList(productos, comedorId);
    }
}
