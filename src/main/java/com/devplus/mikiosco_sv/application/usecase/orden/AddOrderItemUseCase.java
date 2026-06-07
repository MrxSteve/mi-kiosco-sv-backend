package com.devplus.mikiosco_sv.application.usecase.orden;

import com.devplus.mikiosco_sv.domain.exception.BadRequestException;
import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.*;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.*;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.AddOrderItemRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddOrderItemUseCase {

    private final OrdenRepository ordenRepository;
    private final OrdenDetalleRepository detalleRepository;
    private final OrdenDetalleExtraRepository detalleExtraRepository;
    private final ProductoRepository productoRepository;
    private final ProductoComboRepository comboRepository;
    private final ExtraRepository extraRepository;
    private final OrderAssembler assembler;

    @Transactional
    public OrderResponse execute(UUID ordenId, AddOrderItemRequest request, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        var orden = ordenRepository.findByIdAndComedorId(ordenId, comedorId)
                .orElseThrow(() -> NotFoundException.of("Orden", ordenId));

        validateModifiable(orden);

        var producto = productoRepository.findByIdAndComedorId(request.getProductoId(), comedorId)
                .orElseThrow(() -> NotFoundException.of("Producto", request.getProductoId()));

        // Cargar combo si se indicó
        ProductoComboEntity combo = null;
        if (request.getComboId() != null) {
            combo = comboRepository.findByIdAndProductoIdAndComedorId(
                            request.getComboId(), producto.getId(), comedorId)
                    .orElseThrow(() -> NotFoundException.of("Combo", request.getComboId()));
        }

        // Cargar extras
        List<ExtraEntity> extras = List.of();
        if (request.getExtraIds() != null && !request.getExtraIds().isEmpty()) {
            extras = extraRepository.findByIdInAndComedorId(request.getExtraIds(), comedorId);
            if (extras.size() != request.getExtraIds().size()) {
                throw new BadRequestException("Uno o más extras no existen en este comedor");
            }
        }

        // Calcular line_subtotal
        BigDecimal extrasTotal = extras.stream()
                .map(ExtraEntity::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal lineSubtotal;
        if (combo != null) {
            // Con combo: combo_price + sum(extra_prices) * quantity
            lineSubtotal = combo.getComboPrice()
                    .add(extrasTotal.multiply(BigDecimal.valueOf(request.getQuantity())));
        } else {
            // Sin combo: (unit_price + sum(extra_prices)) * quantity
            lineSubtotal = producto.getPrice()
                    .add(extrasTotal)
                    .multiply(BigDecimal.valueOf(request.getQuantity()));
        }

        var detalle = detalleRepository.save(OrdenDetalleEntity.builder()
                .comedorId(comedorId)
                .ordenId(ordenId)
                .productoId(producto.getId())
                .comboId(combo != null ? combo.getId() : null)
                .productNameSnapshot(producto.getName())
                .unitPriceSnapshot(producto.getPrice())
                .comboPriceSnapshot(combo != null ? combo.getComboPrice() : null)
                .quantity(request.getQuantity())
                .lineSubtotal(lineSubtotal)
                .notes(request.getNotes())
                .build());

        // Guardar extras del detalle
        for (ExtraEntity extra : extras) {
            detalleExtraRepository.save(OrdenDetalleExtraEntity.builder()
                    .comedorId(comedorId)
                    .ordenDetalleId(detalle.getId())
                    .extraId(extra.getId())
                    .extraNameSnapshot(extra.getName())
                    .extraPriceSnapshot(extra.getPrice())
                    .build());
        }

        recalcularTotales(orden, ordenId);
        return assembler.assembleSingle(ordenRepository.save(orden));
    }

    private void validateModifiable(OrdenEntity orden) {
        if (orden.getSentToKitchenAt() != null) {
            throw new BadRequestException("La orden ya fue enviada a cocina y no puede modificarse");
        }
    }

    private void recalcularTotales(OrdenEntity orden, UUID ordenId) {
        BigDecimal subtotal = detalleRepository.findByOrdenIdOrderByCreatedAtAsc(ordenId)
                .stream()
                .map(OrdenDetalleEntity::getLineSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        orden.setSubtotal(subtotal);
        orden.setTotalAmount(subtotal.subtract(orden.getDiscountAmount()));
    }
}
