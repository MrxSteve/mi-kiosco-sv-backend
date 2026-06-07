package com.devplus.mikiosco_sv.application.usecase.catalogo;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ProductoRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ToggleProductStatusUseCase {

    private final ProductoRepository productoRepository;
    private final ProductAssembler assembler;

    @Transactional
    public ProductResponse execute(UUID id, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        var producto = productoRepository.findByIdAndComedorId(id, comedorId)
                .orElseThrow(() -> NotFoundException.of("Producto", id));

        GenericStatus newStatus = producto.getStatus() == GenericStatus.ACTIVE
                ? GenericStatus.INACTIVE
                : GenericStatus.ACTIVE;

        producto.setStatus(newStatus);
        return assembler.assembleSingle(productoRepository.save(producto), comedorId);
    }
}
