package com.devplus.mikiosco_sv.application.usecase.comedor;

import com.devplus.mikiosco_sv.domain.exception.ConflictException;
import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.domain.model.SubscriptionStatus;
import com.devplus.mikiosco_sv.domain.model.UserRole;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.ConfiguracionComedorEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.SuscripcionEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.UsuarioEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.ComedorEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.*;
import com.devplus.mikiosco_sv.presentation.dto.request.OnboardingRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.OnboardingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OnboardingUseCase {

    private final ComedorRepository comedorRepository;
    private final ConfiguracionComedorRepository configuracionRepository;
    private final UsuarioRepository usuarioRepository;
    private final PlanSuscripcionRepository planRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public OnboardingResponse execute(OnboardingRequest request) {
        // 1. Email del comedor debe ser único
        if (comedorRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("El email ya está registrado como comedor");
        }

        // 2. Validar que el plan exista y esté activo
        var plan = planRepository
                .findByCodeAndStatus(request.getPlanCode(), GenericStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(
                        "Plan '" + request.getPlanCode() + "' no encontrado o inactivo"));

        // 3. Crear comedor
        var comedor = comedorRepository.save(ComedorEntity.builder()
                .name(request.getComedorName())
                .legalName(request.getLegalName())
                .email(request.getEmail().toLowerCase())
                .phone(request.getPhone())
                .address(request.getAddress())
                .timezone(request.getTimezone() != null
                        ? request.getTimezone() : "America/El_Salvador")
                .status(GenericStatus.ACTIVE)
                .build());

        // 4. Crear configuracion con valores por defecto
        configuracionRepository.save(ConfiguracionComedorEntity.builder()
                .comedor(comedor)
                .build());

        // 5. Crear usuario administrador del comedor
        var adminUser = usuarioRepository.save(UsuarioEntity.builder()
                .comedor(comedor)
                .fullName(request.getAdminName())
                .email(request.getAdminEmail().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.getAdminPassword()))
                .role(UserRole.ADMIN)
                .status(GenericStatus.ACTIVE)
                .build());

        // 6. Crear suscripcion (pago simulado)
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = calculateEndDate(plan.getBillingCycle(), startDate);
        String paymentRef = "SIM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        suscripcionRepository.save(SuscripcionEntity.builder()
                .comedor(comedor)
                .plan(plan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(startDate)
                .endDate(endDate)
                .autoRenew(true)
                .paymentReference(paymentRef)
                .notes("Pago simulado. Confirmar cobro manual antes de activación real.")
                .build());

        return OnboardingResponse.builder()
                .comedorId(comedor.getId())
                .adminUserId(adminUser.getId())
                .paymentReference(paymentRef)
                .message("Comedor registrado exitosamente. "
                        + "Suscripción activa hasta: " + endDate + ". "
                        + "Confirmar pago con referencia: " + paymentRef)
                .build();
    }

    private static LocalDate calculateEndDate(String billingCycle, LocalDate from) {
        return switch (billingCycle) {
            case "quarterly" -> from.plusMonths(3);
            case "yearly"    -> from.plusYears(1);
            default          -> from.plusMonths(1);
        };
    }
}
