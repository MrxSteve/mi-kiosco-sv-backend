package com.devplus.mikiosco_sv.application.usecase.superadmin;

import com.devplus.mikiosco_sv.domain.model.SubscriptionStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.SuscripcionRepository;
import com.devplus.mikiosco_sv.presentation.dto.response.AdminSuscripcionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListSuscripcionesUseCase {

    private final SuscripcionRepository suscripcionRepository;

    @Transactional(readOnly = true)
    public List<AdminSuscripcionResponse> execute(SubscriptionStatus status) {
        var suscripciones = status != null
                ? suscripcionRepository.findAllByStatusOrderByCreatedAtDesc(status)
                : suscripcionRepository.findAllByOrderByCreatedAtDesc();

        return suscripciones.stream()
                .map(s -> AdminSuscripcionResponse.builder()
                        .id(s.getId())
                        .comedorId(s.getComedor().getId())
                        .comedorName(s.getComedor().getName())
                        .comedorEmail(s.getComedor().getEmail())
                        .comedorStatus(s.getComedor().getStatus())
                        .planCode(s.getPlan().getCode())
                        .planName(s.getPlan().getName())
                        .planPrice(s.getPlan().getPrice())
                        .status(s.getStatus())
                        .startDate(s.getStartDate())
                        .endDate(s.getEndDate())
                        .autoRenew(s.isAutoRenew())
                        .paymentReference(s.getPaymentReference())
                        .notes(s.getNotes())
                        .createdAt(s.getCreatedAt())
                        .updatedAt(s.getUpdatedAt())
                        .build())
                .toList();
    }
}
