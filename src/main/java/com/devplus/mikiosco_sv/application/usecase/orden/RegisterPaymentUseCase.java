package com.devplus.mikiosco_sv.application.usecase.orden;

import com.devplus.mikiosco_sv.domain.exception.BadRequestException;
import com.devplus.mikiosco_sv.domain.exception.ConflictException;
import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.PagoEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.OrdenRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.PagoRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.PromocionRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.PaymentRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.RegisterPaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterPaymentUseCase {

    private final OrdenRepository ordenRepository;
    private final PagoRepository pagoRepository;
    private final PromocionRepository promocionRepository;

    @Transactional
    public RegisterPaymentResponse execute(UUID ordenId, PaymentRequest request, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        var orden = ordenRepository.findByIdAndComedorId(ordenId, comedorId)
                .orElseThrow(() -> NotFoundException.of("Orden", ordenId));

        if (pagoRepository.existsByOrdenId(ordenId)) {
            throw new ConflictException("Esta orden ya tiene un pago registrado");
        }

        if (request.getAmountReceived().compareTo(orden.getTotalAmount()) < 0) {
            throw new BadRequestException(
                    "El monto recibido ($" + request.getAmountReceived() +
                    ") es menor al total de la orden ($" + orden.getTotalAmount() + ")");
        }

        BigDecimal change = request.getAmountReceived().subtract(orden.getTotalAmount());
        var now = OffsetDateTime.now();

        var pago = pagoRepository.save(PagoEntity.builder()
                .comedorId(comedorId)
                .ordenId(ordenId)
                .amountReceived(request.getAmountReceived())
                .changeAmount(change)
                .paidAt(now)
                .build());

        // Incrementar uses_count de la promoción si la orden tiene una aplicada
        if (orden.getPromocionId() != null) {
            promocionRepository.findById(orden.getPromocionId()).ifPresent(promo -> {
                promo.setUsesCount(promo.getUsesCount() + 1);
                promocionRepository.save(promo);
            });
        }

        return RegisterPaymentResponse.builder()
                .orderId(ordenId)
                .orderNumber(orden.getOrderNumber())
                .amountReceived(pago.getAmountReceived())
                .changeAmount(pago.getChangeAmount())
                .method(pago.getMethod())
                .paidAt(pago.getPaidAt())
                .build();
    }
}
