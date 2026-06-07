package com.devplus.mikiosco_sv.application.usecase.catalogo;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ProductoExtraRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ProductoRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RemoveProductExtraUseCase {

    private final ProductoRepository productoRepository;
    private final ProductoExtraRepository productoExtraRepository;

    @Transactional
    public void execute(UUID productoId, UUID extraId, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        var producto = productoRepository.findByIdAndComedorId(productoId, comedorId)
                .orElseThrow(() -> NotFoundException.of("Producto", productoId));

        if (!productoExtraRepository.existsByIdProductoIdAndIdExtraId(productoId, extraId)) {
            throw NotFoundException.of("Extra en producto", extraId);
        }

        productoExtraRepository.deleteByIdProductoIdAndIdExtraId(productoId, extraId);

        // Si no quedan extras asignados, actualizar flag
        if (productoExtraRepository.countByIdProductoId(productoId) == 0) {
            producto.setHasExtras(false);
            productoRepository.save(producto);
        }
    }
}
