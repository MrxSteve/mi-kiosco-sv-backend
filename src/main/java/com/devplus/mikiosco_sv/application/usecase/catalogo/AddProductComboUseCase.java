package com.devplus.mikiosco_sv.application.usecase.catalogo;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.ProductoComboEntity;
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
public class AddProductComboUseCase {

    private final ProductoRepository productoRepository;
    private final ProductoComboRepository comboRepository;
    private final ProductAssembler assembler;

    @Transactional
    public ProductResponse execute(UUID productoId, ComboRequest request, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        var producto = productoRepository.findByIdAndComedorId(productoId, comedorId)
                .orElseThrow(() -> NotFoundException.of("Producto", productoId));

        comboRepository.save(ProductoComboEntity.builder()
                .comedorId(comedorId)
                .productoId(productoId)
                .label(request.getLabel())
                .comboQty(request.getComboQty())
                .comboPrice(request.getComboPrice())
                .build());

        if (!producto.isHasCombos()) {
            producto.setHasCombos(true);
            productoRepository.save(producto);
        }

        return assembler.assembleSingle(producto, comedorId);
    }
}
