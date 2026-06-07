package com.devplus.mikiosco_sv.application.usecase.promocion;

import com.devplus.mikiosco_sv.domain.exception.BadRequestException;
import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.PromocionProductoEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.PromocionProductoId;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.PromocionProductoRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.PromocionRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.UpdatePromotionRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.PromotionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdatePromotionUseCase {

    private final PromocionRepository promocionRepository;
    private final PromocionProductoRepository promocionProductoRepository;
    private final PromotionAssembler assembler;

    @Transactional
    public PromotionResponse execute(UUID id, UpdatePromotionRequest request, AuthenticatedUser caller) {
        var promo = promocionRepository.findByIdAndComedorId(id, caller.comedorId())
                .orElseThrow(() -> NotFoundException.of("Promoción", id));

        if (request.getName() != null)          promo.setName(request.getName());
        if (request.getDiscountKind() != null)  promo.setDiscountKind(request.getDiscountKind());
        if (request.getDiscountValue() != null) promo.setDiscountValue(request.getDiscountValue());
        if (request.getStartsAt() != null)      promo.setStartsAt(request.getStartsAt());
        if (request.getEndsAt() != null)        promo.setEndsAt(request.getEndsAt());
        if (request.getMaxUses() != null)       promo.setMaxUses(request.getMaxUses());

        if (request.getAppliesToAll() != null) {
            boolean newValue = request.getAppliesToAll();
            if (!newValue && (request.getProductIds() == null || request.getProductIds().isEmpty())) {
                throw new BadRequestException(
                        "Debe especificar al menos un producto cuando appliesToAll es false");
            }
            promo.setAppliesToAll(newValue);
        }

        promocionRepository.save(promo);

        // Reemplazar links de productos si se envían
        if (request.getProductIds() != null) {
            promocionProductoRepository.deleteByIdPromocionId(promo.getId());
            saveProductLinks(promo.getId(), request.getProductIds());
        }

        return assembler.toResponse(promo);
    }

    private void saveProductLinks(UUID promocionId, List<UUID> productIds) {
        productIds.forEach(pid ->
                promocionProductoRepository.save(PromocionProductoEntity.builder()
                        .id(new PromocionProductoId(promocionId, pid))
                        .build()));
    }
}
