package com.devplus.mikiosco_sv.application.usecase.orden;

import com.devplus.mikiosco_sv.domain.exception.BadRequestException;
import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.domain.model.DiscountType;
import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.OrdenDetalleEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.*;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.ApplyOrderPromotionRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplyOrderPromotionUseCase {

    private final OrdenRepository ordenRepository;
    private final OrdenDetalleRepository detalleRepository;
    private final PromocionRepository promocionRepository;
    private final PromocionProductoRepository promocionProductoRepository;
    private final OrderAssembler assembler;

    @Transactional
    public OrderResponse execute(UUID ordenId, ApplyOrderPromotionRequest request, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        var orden = ordenRepository.findByIdAndComedorId(ordenId, comedorId)
                .orElseThrow(() -> NotFoundException.of("Orden", ordenId));

        if (orden.getSentToKitchenAt() != null) {
            throw new BadRequestException("La orden ya fue enviada a cocina");
        }

        var promo = promocionRepository.findByComedorIdAndCodeIgnoreCase(comedorId, request.getCode())
                .orElseThrow(() -> new BadRequestException("Código de promoción no encontrado"));

        if (promo.getStatus() != GenericStatus.ACTIVE) {
            throw new BadRequestException("La promoción no está activa");
        }

        var now = OffsetDateTime.now();
        if (promo.getStartsAt() != null && now.isBefore(promo.getStartsAt())) {
            throw new BadRequestException("La promoción aún no ha comenzado");
        }
        if (promo.getEndsAt() != null && now.isAfter(promo.getEndsAt())) {
            throw new BadRequestException("La promoción ha expirado");
        }
        if (promo.getMaxUses() != null && promo.getUsesCount() >= promo.getMaxUses()) {
            throw new BadRequestException("La promoción ha alcanzado su límite de usos");
        }

        if (!promo.isAppliesToAll()) {
            List<UUID> productIds = detalleRepository.findByOrdenIdOrderByCreatedAtAsc(ordenId)
                    .stream().map(OrdenDetalleEntity::getProductoId).toList();
            if (productIds.isEmpty() || !promocionProductoRepository
                    .existsByIdPromocionIdAndIdProductoIdIn(promo.getId(), productIds)) {
                throw new BadRequestException("La promoción no aplica a los productos de esta orden");
            }
        }

        BigDecimal discount = promo.getDiscountKind() == DiscountType.PERCENTAGE
                ? orden.getSubtotal().multiply(promo.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : promo.getDiscountValue();

        discount = discount.min(orden.getSubtotal());

        orden.setPromocionId(promo.getId());
        orden.setDiscountAmount(discount);
        orden.setTotalAmount(orden.getSubtotal().subtract(discount));

        return assembler.assembleSingle(ordenRepository.save(orden));
    }
}
