package com.devplus.mikiosco_sv.application.usecase.superadmin;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.domain.model.SubscriptionStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ComedorRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.SuscripcionRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.UsuarioRepository;
import com.devplus.mikiosco_sv.presentation.dto.response.AdminComedorDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetComedorAdminDetailUseCase {

    private final ComedorRepository comedorRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public AdminComedorDetailResponse execute(UUID comedorId) {
        var comedor = comedorRepository.findById(comedorId)
                .orElseThrow(() -> NotFoundException.of("Comedor", comedorId));

        long userCount = usuarioRepository.countByComedor_Id(comedorId);

        var subOpt = suscripcionRepository.findByComedor_IdAndStatus(comedorId, SubscriptionStatus.ACTIVE);

        AdminComedorDetailResponse.SubscriptionInfo subInfo = subOpt.map(s ->
                AdminComedorDetailResponse.SubscriptionInfo.builder()
                        .suscripcionId(s.getId())
                        .planName(s.getPlan().getName())
                        .planPrice(s.getPlan().getPrice())
                        .status(s.getStatus())
                        .startDate(s.getStartDate())
                        .endDate(s.getEndDate())
                        .autoRenew(s.isAutoRenew())
                        .paymentReference(s.getPaymentReference())
                        .build()
        ).orElse(null);

        return AdminComedorDetailResponse.builder()
                .id(comedor.getId())
                .name(comedor.getName())
                .legalName(comedor.getLegalName())
                .email(comedor.getEmail())
                .phone(comedor.getPhone())
                .address(comedor.getAddress())
                .timezone(comedor.getTimezone())
                .status(comedor.getStatus())
                .userCount(userCount)
                .createdAt(comedor.getCreatedAt())
                .subscription(subInfo)
                .build();
    }
}
