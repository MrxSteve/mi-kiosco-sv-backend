package com.devplus.mikiosco_sv.application.usecase.superadmin;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.SuscripcionRepository;
import com.devplus.mikiosco_sv.presentation.dto.request.ExtendSuscripcionRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.AdminSuscripcionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExtendSuscripcionUseCase {

    private final SuscripcionRepository suscripcionRepository;

    @Transactional
    public AdminSuscripcionResponse execute(UUID id, ExtendSuscripcionRequest request) {
        var s = suscripcionRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Suscripción", id));

        s.setEndDate(request.getNewEndDate());
        if (request.getNotes() != null) {
            s.setNotes(request.getNotes());
        }

        var saved = suscripcionRepository.save(s);
        return AdminSuscripcionResponse.builder()
                .id(saved.getId())
                .comedorId(saved.getComedor().getId())
                .comedorName(saved.getComedor().getName())
                .comedorEmail(saved.getComedor().getEmail())
                .comedorStatus(saved.getComedor().getStatus())
                .planCode(saved.getPlan().getCode())
                .planName(saved.getPlan().getName())
                .planPrice(saved.getPlan().getPrice())
                .status(saved.getStatus())
                .startDate(saved.getStartDate())
                .endDate(saved.getEndDate())
                .autoRenew(saved.isAutoRenew())
                .paymentReference(saved.getPaymentReference())
                .notes(saved.getNotes())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }
}
