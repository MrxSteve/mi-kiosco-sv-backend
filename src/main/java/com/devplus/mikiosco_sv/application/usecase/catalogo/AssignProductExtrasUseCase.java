package com.devplus.mikiosco_sv.application.usecase.catalogo;

import com.devplus.mikiosco_sv.domain.exception.BadRequestException;
import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.ProductoExtraEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.ProductoExtraId;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ExtraRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ProductoExtraRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ProductoRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.AssignExtrasRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignProductExtrasUseCase {

    private final ProductoRepository productoRepository;
    private final ExtraRepository extraRepository;
    private final ProductoExtraRepository productoExtraRepository;
    private final ProductAssembler assembler;

    @Transactional
    public ProductResponse execute(UUID productoId, AssignExtrasRequest request, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        var producto = productoRepository.findByIdAndComedorId(productoId, comedorId)
                .orElseThrow(() -> NotFoundException.of("Producto", productoId));

        // Validar que todos los extras pertenezcan al comedor
        var extras = extraRepository.findByIdInAndComedorId(request.getExtraIds(), comedorId);
        if (extras.size() != request.getExtraIds().size()) {
            throw new BadRequestException("Uno o más extras no existen o no pertenecen a este comedor");
        }

        // Asignar solo los que no estén ya asignados (idempotente)
        for (UUID extraId : request.getExtraIds()) {
            if (!productoExtraRepository.existsByIdProductoIdAndIdExtraId(productoId, extraId)) {
                productoExtraRepository.save(ProductoExtraEntity.builder()
                        .id(new ProductoExtraId(productoId, extraId))
                        .build());
            }
        }

        if (!producto.isHasExtras()) {
            producto.setHasExtras(true);
            productoRepository.save(producto);
        }

        return assembler.assembleSingle(producto, comedorId);
    }
}
