package com.devplus.mikiosco_sv.application.usecase.promocion;

import com.devplus.mikiosco_sv.infrastructure.persistence.entity.PromocionEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.PromocionProductoId;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.PromocionProductoRepository;
import com.devplus.mikiosco_sv.presentation.dto.response.PromotionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PromotionAssembler {

    private final PromocionProductoRepository promocionProductoRepository;

    public PromotionResponse toResponse(PromocionEntity entity) {
        List<UUID> productIds = promocionProductoRepository
                .findByIdPromocionId(entity.getId())
                .stream()
                .map(pp -> pp.getId().getProductoId())
                .toList();

        return PromotionResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .discountKind(entity.getDiscountKind())
                .discountValue(entity.getDiscountValue())
                .startsAt(entity.getStartsAt())
                .endsAt(entity.getEndsAt())
                .maxUses(entity.getMaxUses())
                .usesCount(entity.getUsesCount())
                .appliesToAll(entity.isAppliesToAll())
                .productIds(productIds)
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
