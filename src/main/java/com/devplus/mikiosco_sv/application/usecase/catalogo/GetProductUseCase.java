package com.devplus.mikiosco_sv.application.usecase.catalogo;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ProductoRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetProductUseCase {

    private final ProductoRepository productoRepository;
    private final ProductAssembler assembler;

    @Transactional(readOnly = true)
    public ProductResponse execute(UUID id, AuthenticatedUser caller) {
        var producto = productoRepository.findByIdAndComedorId(id, caller.comedorId())
                .orElseThrow(() -> NotFoundException.of("Producto", id));
        return assembler.assembleSingle(producto, caller.comedorId());
    }
}
