package com.devplus.mikiosco_sv.application.usecase.catalogo;

import com.devplus.mikiosco_sv.domain.exception.ConflictException;
import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.CategoriaRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ProductoRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.UpdateProductRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateProductUseCase {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductAssembler assembler;

    @Transactional
    public ProductResponse execute(UUID id, UpdateProductRequest request, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        var producto = productoRepository.findByIdAndComedorId(id, comedorId)
                .orElseThrow(() -> NotFoundException.of("Producto", id));

        if (request.getName() != null) {
            if (productoRepository.existsByComedorIdAndNameIgnoreCaseAndIdNot(comedorId, request.getName(), id)) {
                throw new ConflictException("Ya existe un producto con ese nombre");
            }
            producto.setName(request.getName());
        }
        if (request.getDescription() != null) producto.setDescription(request.getDescription());
        if (request.getPrice() != null)       producto.setPrice(request.getPrice());
        if (request.getImageUrl() != null)    producto.setImageUrl(request.getImageUrl());

        if (request.getCategoryId() != null) {
            categoriaRepository.findByIdAndComedorId(request.getCategoryId(), comedorId)
                    .orElseThrow(() -> NotFoundException.of("Categoría", request.getCategoryId()));
            producto.setCategoriaId(request.getCategoryId());
        }

        return assembler.assembleSingle(productoRepository.save(producto), comedorId);
    }
}
