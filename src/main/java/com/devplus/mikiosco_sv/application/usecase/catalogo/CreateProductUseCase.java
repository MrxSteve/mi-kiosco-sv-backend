package com.devplus.mikiosco_sv.application.usecase.catalogo;

import com.devplus.mikiosco_sv.domain.exception.ConflictException;
import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.ProductoEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.CategoriaRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ProductoRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.CreateProductRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateProductUseCase {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductAssembler assembler;

    @Transactional
    public ProductResponse execute(CreateProductRequest request, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        if (productoRepository.existsByComedorIdAndNameIgnoreCase(comedorId, request.getName())) {
            throw new ConflictException("Ya existe un producto con ese nombre");
        }

        // Validar que la categoría pertenezca al comedor
        categoriaRepository.findByIdAndComedorId(request.getCategoryId(), comedorId)
                .orElseThrow(() -> NotFoundException.of("Categoría", request.getCategoryId()));

        var entity = ProductoEntity.builder()
                .comedorId(comedorId)
                .categoriaId(request.getCategoryId())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .build();

        return assembler.assembleSingle(productoRepository.save(entity), comedorId);
    }
}
