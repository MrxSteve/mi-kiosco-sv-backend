package com.devplus.mikiosco_sv.application.usecase.promocion;

import com.devplus.mikiosco_sv.domain.exception.BadRequestException;
import com.devplus.mikiosco_sv.domain.exception.ConflictException;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.PromocionEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.PromocionProductoEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.PromocionProductoId;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.PromocionProductoRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.PromocionRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.CreatePromotionRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.PromotionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreatePromotionUseCase {

    private final PromocionRepository promocionRepository;
    private final PromocionProductoRepository promocionProductoRepository;
    private final PromotionAssembler assembler;

    @Transactional
    public PromotionResponse execute(CreatePromotionRequest request, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        if (promocionRepository.existsByComedorIdAndCodeIgnoreCase(comedorId, request.getCode())) {
            throw new ConflictException("Ya existe una promoción con el código '" + request.getCode() + "'");
        }

        validateProductIds(request.getAppliesToAll(), request.getProductIds());

        var promo = promocionRepository.save(PromocionEntity.builder()
                .comedorId(comedorId)
                .code(request.getCode().toUpperCase())
                .name(request.getName())
                .discountKind(request.getDiscountKind())
                .discountValue(request.getDiscountValue())
                .startsAt(request.getStartsAt())
                .endsAt(request.getEndsAt())
                .maxUses(request.getMaxUses())
                .appliesToAll(request.getAppliesToAll())
                .build());

        saveProductLinks(promo.getId(), request.getProductIds());

        return assembler.toResponse(promo);
    }

    private void validateProductIds(boolean appliesToAll, List<UUID> productIds) {
        if (!appliesToAll && (productIds == null || productIds.isEmpty())) {
            throw new BadRequestException(
                    "Debe especificar al menos un producto cuando appliesToAll es false");
        }
    }

    private void saveProductLinks(UUID promocionId, List<UUID> productIds) {
        if (productIds == null || productIds.isEmpty()) return;
        productIds.forEach(pid ->
                promocionProductoRepository.save(PromocionProductoEntity.builder()
                        .id(new PromocionProductoId(promocionId, pid))
                        .build()));
    }
}
