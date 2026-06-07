package com.devplus.mikiosco_sv.application.usecase.catalogo;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ProductoComboRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ProductoRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteProductComboUseCase {

    private final ProductoRepository productoRepository;
    private final ProductoComboRepository comboRepository;

    @Transactional
    public void execute(UUID productoId, UUID comboId, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        var producto = productoRepository.findByIdAndComedorId(productoId, comedorId)
                .orElseThrow(() -> NotFoundException.of("Producto", productoId));

        var combo = comboRepository.findByIdAndProductoIdAndComedorId(comboId, productoId, comedorId)
                .orElseThrow(() -> NotFoundException.of("Combo", comboId));

        comboRepository.delete(combo);

        // Si no quedan combos activos, actualizar flag del producto
        if (!comboRepository.existsByProductoIdAndComedorIdAndStatus(productoId, comedorId, GenericStatus.ACTIVE)) {
            producto.setHasCombos(false);
            productoRepository.save(producto);
        }
    }
}
