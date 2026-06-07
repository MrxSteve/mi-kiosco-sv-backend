package com.devplus.mikiosco_sv.application.usecase.catalogo;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ProductoComboRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ProductoRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.ComboRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateProductComboUseCase {

    private final ProductoRepository productoRepository;
    private final ProductoComboRepository comboRepository;
    private final ProductAssembler assembler;

    @Transactional
    public ProductResponse execute(UUID productoId, UUID comboId, ComboRequest request, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        var producto = productoRepository.findByIdAndComedorId(productoId, comedorId)
                .orElseThrow(() -> NotFoundException.of("Producto", productoId));

        var combo = comboRepository.findByIdAndProductoIdAndComedorId(comboId, productoId, comedorId)
                .orElseThrow(() -> NotFoundException.of("Combo", comboId));

        combo.setLabel(request.getLabel());
        combo.setComboQty(request.getComboQty());
        combo.setComboPrice(request.getComboPrice());
        comboRepository.save(combo);

        return assembler.assembleSingle(producto, comedorId);
    }
}
