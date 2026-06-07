package com.devplus.mikiosco_sv.application.usecase.promocion;

import com.devplus.mikiosco_sv.domain.model.DiscountType;
import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.PromocionProductoRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.PromocionRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.ValidatePromotionRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.ValidatePromotionResponse;
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
public class ValidatePromotionUseCase {

    private final PromocionRepository promocionRepository;
    private final PromocionProductoRepository promocionProductoRepository;

    @Transactional(readOnly = true)
    public ValidatePromotionResponse execute(ValidatePromotionRequest request, AuthenticatedUser caller) {

        // 1. Existe el código para este comedor
        var promoOpt = promocionRepository
                .findByComedorIdAndCodeIgnoreCase(caller.comedorId(), request.getCode());
        if (promoOpt.isEmpty()) {
            return invalid("Código de promoción no encontrado");
        }
        var promo = promoOpt.get();

        // 2. Está activa
        if (promo.getStatus() != GenericStatus.ACTIVE) {
            return invalid("La promoción no está activa");
        }

        // 3. Dentro del rango de fechas
        var now = OffsetDateTime.now();
        if (promo.getStartsAt() != null && now.isBefore(promo.getStartsAt())) {
            return invalid("La promoción aún no ha comenzado");
        }
        if (promo.getEndsAt() != null && now.isAfter(promo.getEndsAt())) {
            return invalid("La promoción ha expirado");
        }

        // 4. No ha superado el límite de usos
        if (promo.getMaxUses() != null && promo.getUsesCount() >= promo.getMaxUses()) {
            return invalid("La promoción ha alcanzado su límite de usos");
        }

        // 5. Si no aplica a todo, al menos un producto de la orden debe estar en la lista
        if (!promo.isAppliesToAll()) {
            List<UUID> orderProductIds = request.getProductIds();
            if (orderProductIds == null || orderProductIds.isEmpty()) {
                return invalid("La promoción no aplica a los productos de esta orden");
            }
            boolean hasMatch = promocionProductoRepository
                    .existsByIdPromocionIdAndIdProductoIdIn(promo.getId(), orderProductIds);
            if (!hasMatch) {
                return invalid("La promoción no aplica a los productos de esta orden");
            }
        }

        // Calcular descuento
        BigDecimal discount = calculateDiscount(promo.getDiscountKind(), promo.getDiscountValue(),
                request.getOrderSubtotal());

        return ValidatePromotionResponse.builder()
                .valid(true)
                .discountAmount(discount)
                .message("Promoción válida: " + promo.getName())
                .build();
    }

    private BigDecimal calculateDiscount(DiscountType kind, BigDecimal value, BigDecimal subtotal) {
        BigDecimal discount = kind == DiscountType.PERCENTAGE
                ? subtotal.multiply(value).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : value;
        // El descuento nunca puede superar el subtotal
        return discount.min(subtotal).setScale(2, RoundingMode.HALF_UP);
    }

    private ValidatePromotionResponse invalid(String message) {
        return ValidatePromotionResponse.builder()
                .valid(false)
                .discountAmount(BigDecimal.ZERO)
                .message(message)
                .build();
    }
}
